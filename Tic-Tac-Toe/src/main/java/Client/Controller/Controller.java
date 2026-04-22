package Client.Controller;

import Client.Model.Model;
import Client.View.View;
import java.util.concurrent.BlockingQueue;

public class Controller {
    private Model model;
    private View view = new View(this);
    private final BlockingQueue<String> queue;
    private char[][] localField = new char[3][3];
    private char myMark = ' ';
    private String lastPendingMove = "";


    public Controller(Model model, BlockingQueue<String> queue) {
        this.model = model;
        this.queue = queue;
        initField();
    }


    private void initField() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) localField[i][j] = '.';
    }


    public void startGame() {
        if (this.model.connect()) {
            new PollQueue(queue).start();
        } else {
            this.view.showMessage("Error starting the game");
            this.view.displayInterface();
        }
    }


    public void makeMove(String move) {
        try {
            String[] parts = move.split(" ");
            int r = Integer.parseInt(parts[0]) - 1;
            int c = Integer.parseInt(parts[1]) - 1;

            lastPendingMove = r + "," + c;

            model.sendRequest("MOVE " + r + "," + c);
        } catch (Exception e) {
            view.showMessage("Invalid format!");
            view.displayGameInterface(localField, myMark);
        }
    }


    private class PollQueue extends Thread {
        private final BlockingQueue<String> queue;

        public PollQueue(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String data = queue.take();
                    process(data);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void process(String data) {
            if (data.equals("WAIT")) {
                view.showMessage("Waiting for opponent to connect...");
            }
            else if (data.equals("START_FIRST")) {
                myMark = 'X';
                view.showMessage("Game started! You are X. Your turn!");
                view.displayGameInterface(localField, myMark);
            }
            else if (data.equals("START_SECOND")) {
                myMark = 'O';
                view.showMessage("Game started! You are O. Wait for X move.");
            }
            else if (data.equals("VALID")) {
                updateField(lastPendingMove, myMark);
                view.showMessage("Move accepted. Waiting for opponent...");

                view.printOnlyField(localField);
            }
            else if (data.equals("INVALID")) {
                view.showMessage("This cell is already taken or move is invalid! Try again.");
                view.displayGameInterface(localField, myMark);
            }
            else if (data.startsWith("YOUR_TURN ")) {
                String coords = data.substring(10);
                updateField(coords, (myMark == 'X' ? 'O' : 'X'));

                view.showMessage("Opponent moved! Your turn.");
                view.displayGameInterface(localField, myMark);
            }
            else if (data.equals("NOT_YOUR_TURN")) {
                view.showMessage("It's not your turn! Please wait.");
            }
            else if (data.equals("WIN")) {
                view.showMessage("CONGRATULATIONS! YOU WON!");
                view.displayInterface();
            }
            else if (data.equals("LOSS")) {
                view.showMessage("GAME OVER. YOU LOST.");
                view.displayInterface();
            }
        }


        private void updateField(String coords, char mark) {
            String[] p = coords.split(",");
            int r = Integer.parseInt(p[0]);
            int c = Integer.parseInt(p[1]);
            localField[r][c] = mark;
        }
    }


    public void start() {
        this.view.displayInterface();
    }
}