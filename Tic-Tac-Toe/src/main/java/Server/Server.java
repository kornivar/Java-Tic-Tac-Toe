package Server;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;
import java.util.LinkedList;

public class Server {
    public static final int PORT = 8080;
    public static LinkedList<ServerClientHandler> serverList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server is up!");
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerClientHandler(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }
}


