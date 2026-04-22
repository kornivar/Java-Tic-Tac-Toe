package Server;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;
import java.util.LinkedList;

public class Server {
    public static final int PORT = 8080;
    public static LinkedList<ServerClientHandler> serverList = new LinkedList<>();
    private static ServerClientHandler waitingPlayer = null;

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server is up!");

            while (true) {
                Socket socket = server.accept();
                try {
                    ServerClientHandler newPlayer = new ServerClientHandler(socket);
                    serverList.add(newPlayer);

                    if (waitingPlayer == null) {
                        waitingPlayer = newPlayer;
                        System.out.println("Player 1 connected, waiting for Player 2...");
                    } else {
                        System.out.println("Player 2 connected, starting session.");

                        ServerClientHandler.GameSession session = new ServerClientHandler.GameSession();

                        waitingPlayer.setGameContext(newPlayer, session, "X");
                        newPlayer.setGameContext(waitingPlayer, session, "O");

                        waitingPlayer.send("START_FIRST");
                        newPlayer.send("START_SECOND");

                        waitingPlayer = null;
                    }
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }
}