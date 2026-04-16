package Client;

import Client.Model.Model;
import Client.Controller.Controller;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<>();

        Model model = new Model("localhost", 8080, sharedQueue);

        Controller controller = new Controller(model, sharedQueue);

        controller.start();
    }
}