package Client;

import java.io.*;
import java.net.Socket;

public class test {
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedReader in;
    private BufferedWriter out;
    public static final int PORT = 8080;
    public static final String IP = "localhost";

    public static void main(String[] args) {
        new test();
    }

    public test() {
        try {
            clientSocket = new Socket(IP, PORT);
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            new ReadMsg().start();
            new WriteMsg().start();

        } catch (IOException e) {
            downService();
        }
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
                    System.out.println(str);
                }
            } catch (IOException e) {
                downService();
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