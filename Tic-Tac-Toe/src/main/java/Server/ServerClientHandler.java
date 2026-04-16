package Server;

import java.io.*;
import java.net.Socket;

public class ServerClientHandler extends Thread{
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerClientHandler(Socket socket) throws IOException{
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
        start();
    }

    @Override
    public void run(){
        String word;
        try{
            while (true){
                word = in.readLine();
                if(word.equals("stop")){
                    downService();
                    break;
                }
                for(ServerClientHandler vr : Server.serverList){
                    if(vr == this){
                        out.write("Message successfully sent!" + "\n");
                        out.flush();
                        continue;
                    }
                    vr.send(word);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void send(String msg){
        try{
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void downService(){
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();

                Server.serverList.remove(this);

                System.out.println("Client connections on the server side closed.");
            }
        } catch (IOException ignored) {}
    }
}


