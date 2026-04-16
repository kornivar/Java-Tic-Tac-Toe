package Client.Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.io.*;
import java.util.concurrent.BlockingQueue;

public class Model {
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedReader in;
    private BufferedWriter out;
    private final String IP;
    private final int PORT;
    private BlockingQueue<String> queue;

    public Model(String ip, int port, BlockingQueue<String> queue) {
        this.IP = ip;
        this.PORT = port;
        this.queue = queue;
    }

//    private int[][] play_field = {
//            {0, 0, 0},
//            {0, 0, 0},
//            {0, 0, 0}
//    };

//    public int current_turn = 1;
//
//    public boolean startGame(String sign){
//        if (!Objects.equals(sign, "X") && !Objects.equals(sign, "O")){
//            return false;
//        }
//
//        if(sign.equals("X")){
//            this.current_turn = 1;
//        }else{
//            this.current_turn = 2;
//        }
//
//        return true;
//    }


//    public char[][] getField(){
//        char[][] char_field = new char[3][3];
//
//        for(int i = 0; i < play_field.length; i++){
//            for(int j = 0; j < 3; j++){
//                if(play_field[i][j] == 0){
//                    char_field[i][j] = '-';
//                }else if(play_field[i][j] == 1){
//                    char_field[i][j] = 'X';
//                }else{
//                    char_field[i][j] = 'O';
//                }
//            }
//        }
//
//        return char_field;
//    }


//    public boolean makeMove(String move){
//        String[] parts = move.trim().split("\\s+");
//
//        if (parts.length != 2) {
//            return false;
//        }
//
//        try {
//            int row = Integer.parseInt(parts[0]);
//            int col = Integer.parseInt(parts[1]);
//
//            if (row < 1 || row > 3 || col < 1 || col > 3) {
//                return false;
//            }
//
//            if(this.current_turn == 1){
//                play_field[row-1][col-1] = 1;
//                this.current_turn = 2;
//            }else{
//                play_field[row-1][col-1] = 2;
//                this.current_turn = 1;
//            }
//            return true;
//
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }


//    public int checkWinner(){
//        for (int i = 0; i < 3; i++) {
//            if (play_field[i][0] != 0 && play_field[i][0] == play_field[i][1] && play_field[i][0] == play_field[i][2]) return play_field[i][0];
//            if (play_field[0][i] != 0 && play_field[0][i] == play_field[1][i] && play_field[0][i] == play_field[2][i]) return play_field[0][i];
//        }
//        if (play_field[0][0] != 0 && play_field[0][0] == play_field[1][1] && play_field[0][0] == play_field[2][2]) return play_field[0][0];
//        if (play_field[0][2] != 0 && play_field[0][2] == play_field[1][1] && play_field[0][2] == play_field[2][0]) return play_field[0][2];
//
//        boolean hasEmptyCells = false;
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (play_field[i][j] == 0) {
//                    hasEmptyCells = true;
//                    break;
//                }
//            }
//        }
//
//        if (hasEmptyCells) {
//            return 0;
//        } else {
//            return -1;
//        }
//    }


    public boolean connect() {
        boolean connected = false;

        while (!connected) {
            try {
                System.out.println("Trying to connect to the server " + IP + ":" + PORT + "...");
                clientSocket = new Socket(IP, PORT);

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Connected successfully!");
                connected = true;

                new ReadMsg().start();
                new WriteMsg().start();

            } catch (IOException e) {
                System.err.println("Server not responding. Retrying in 2 seconds...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return true;
    }


    private void downService() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
                System.out.println("Client connections closed.");
            }
        } catch (IOException ignored) {}
    }


    private class ReadMsg extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine();
                    if (str == null || str.equals("stop")) {
                        downService();
                        break;
                    }
                    System.out.println("Client received a package: " + str);
                    queue.put(str);
                }
            } catch (IOException e) {
                downService();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private class WriteMsg extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String userWord = reader.readLine();
                    if (userWord.equals("stop")) {
                        out.write("stop\n");
                        out.flush();
                        downService();
                        break;
                    } else {
                        out.write(userWord + "\n");
                    }
                    out.flush();
                } catch (IOException e) {
                    downService();
                    break;
                }
            }
        }
    }
}
