package Client.Controller;
import Client.Model.Model;
import Client.View.View;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Controller {
    private Model model;
    private View view = new View(this);
    private BlockingQueue<String> queue;


    public Controller(Model model, BlockingQueue<String> queue) {
        this.model = model;
        this.queue = queue;
    }


    public void setSign(String sign){
        boolean result = this.model.connect();
        if (result){
            new PollQueue().start();
        }else{
            this.view.showMessage("Error setting sign. Make sure you are entering X or O!");
            this.view.displayInterface();
        }
    }


    private class PollQueue extends Thread{
        @Override
        public void run() {

        }
    }

//    public void displayGameInterface(){
//        char[][] char_field = this.model.getField();
//        char player = 'X';
//        if(this.model.current_turn == 1){ player = 'X';}
//        else{ player = 'O';}
//        this.view.displayGameInterface(char_field, player);
//    }
//
//    public void makeMove(String move){
//        boolean result = this.model.makeMove(move);
//        if(result){
//            int winner = this.model.checkWinner();
//            if(winner != 0 && winner != -1){
//                if(winner == 1){
//                    this.view.showMessage("Player X won the game!");
//                    this.view.displayInterface();
//                }else{
//                    this.view.showMessage("Player O won the game!");
//                    this.view.displayInterface();
//                }
//            }else if (winner == 0){
//                this.displayGameInterface();
//            }else{
//                this.view.showMessage("DRAW!");
//                this.view.displayInterface();
//            }
//        }else{
//            this.view.showMessage("Error. Make sure to enter your move with two digits divided by space!");
//            this.displayGameInterface();
//        }
//    }


    public void start(){
        this.view.displayInterface();
    }
}

