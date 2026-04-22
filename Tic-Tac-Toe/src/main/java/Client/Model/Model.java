package Client.Model;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Model {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private final String IP;
    private final int PORT;
    private final BlockingQueue<String> queue;


    public Model(String ip, int port, BlockingQueue<String> queue) {
        this.IP = ip;
        this.PORT = port;
        this.queue = queue;
    }


    public void sendRequest(String msg) {
        try {
            if (out != null) {
                out.write(msg + "\n");
                out.flush();
            }
        } catch (IOException e) {
            downService();
        }
    }


    public boolean connect() {
        try {
            System.out.println("Connecting to " + IP + ":" + PORT + "...");
            clientSocket = new Socket(IP, PORT);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            new ReadMsg().start();
            return true;
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }


    private void downService() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }


    private class ReadMsg extends Thread {
        @Override
        public void run() {
            try {
                String str;
                while ((str = in.readLine()) != null) {
                    if (str.equals("stop")) break;
                    queue.put(str);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Connection lost.");
            } finally {
                downService();
            }
        }
    }
}