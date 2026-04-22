package Server;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServerClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private ServerClientHandler opponent;

    private GameSession session;
    private String myMark;

    private String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=test_db;integratedSecurity=true;trustServerCertificate=true;";

    public ServerClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    public void setGameContext(ServerClientHandler opponent, GameSession session, String mark) {
        this.opponent = opponent;
        this.session = session;
        this.myMark = mark;
    }

    @Override
    public void run() {
        try {
            while (opponent == null || session == null) {
                send("WAIT");
                try { Thread.sleep(2000); } catch (InterruptedException e) { break; }
            }

            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("MOVE ")) {
                    String moveData = input.substring(5);

                    if (handleMove(moveData)) {
                        saveMoveToDB(moveData);

                        if (session.checkWin()) {
                            send("WIN");
                            opponent.send("LOSS");
                            break;
                        } else {
                            send("VALID");
                            opponent.send("YOUR_TURN " + moveData);
                        }
                    } else {
                        send("INVALID");
                    }
                }

                if (input.equals("stop")) {
                    break;
                }
            }
        } catch (IOException e) {
        } finally {
            downService();
        }
    }

    private boolean handleMove(String moveData) {
        if (session == null || opponent == null) return false;

        try {
            String[] coords = moveData.split(",");
            int r = Integer.parseInt(coords[0]);
            int c = Integer.parseInt(coords[1]);

            return session.validateAndMove(r, c, myMark);
        } catch (Exception e) {
            return false;
        }
    }

    private void saveMoveToDB(String move) {
        String sql = "INSERT INTO game_moves (player_identifier, move_data) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, myMark);
            pstmt.setString(2, move);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            downService();
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                Server.serverList.remove(this);
                if (opponent != null) opponent.send("OPPONENT_LEFT");
            }
        } catch (IOException ignored) {}
    }

    public static class GameSession {
        private final String[][] board = new String[3][3];
        private String currentTurnMark = "X";

        public synchronized String getCurrentTurnMark() {
            return currentTurnMark;
        }

        public synchronized boolean validateAndMove(int row, int col, String mark) {
            if (!mark.equals(currentTurnMark)) {
                return false;
            }

            if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != null) {
                return false;
            }

            board[row][col] = mark;

            currentTurnMark = mark.equals("X") ? "O" : "X";
            return true;
        }

        public synchronized boolean checkWin() {
            for (int i = 0; i < 3; i++) {
                if (compare(board[i][0], board[i][1], board[i][2])) return true;
                if (compare(board[0][i], board[1][i], board[2][i])) return true;
            }
            return compare(board[0][0], board[1][1], board[2][2]) ||
                    compare(board[0][2], board[1][1], board[2][0]);
        }

        private boolean compare(String a, String b, String c) {
            return a != null && a.equals(b) && b.equals(c);
        }
    }
}

