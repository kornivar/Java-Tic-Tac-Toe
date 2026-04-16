package Client.View;
import java.util.Scanner;
import Client.Controller.Controller;

public class View {
    private Controller controller = null;
    Scanner scanner = new Scanner(System.in);


    public View(Controller Controller){
        this.controller = Controller;
    }


    public void displayInterface() {
        System.out.println("\n--- Tic-Tac-Toe Menu ---");
        System.out.println("1. Start the game");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                this.startGame();
                break;
            case "0":
                System.out.println("Exiting game. Goodbye!");
                break;
            default:
                System.out.println("Error: Invalid option. Please try again.");
        }
    }


    public void displayGameInterface(char[][] field, char current_move){
        System.out.println("\n--- Game field ---");
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                System.out.print(field[i][j] +" ");
            }
            System.out.println();
        }
        System.out.print("Player " + current_move + " move (1 2, 2 3):");
        String move = scanner.nextLine();
        this.controller.makeMove(move);
    }


    public void startGame(){
        System.out.println("Choose sign for player 1 (X or O): ");
        String sign = scanner.nextLine();
        this.controller.setSign(sign);
    }


    public void showMessage(String message){
        System.out.println(message);
    }
}
