package Client.View;

import java.util.Scanner;
import Client.Controller.Controller;

public class View {
    private final Controller controller;
    private final Scanner scanner = new Scanner(System.in);

    public View(Controller controller) {
        this.controller = controller;
    }


    public void displayInterface() {
        System.out.println("\n--- Tic-Tac-Toe Menu ---");
        System.out.println("1. Start the game");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine();
        if ("1".equals(choice)) {
            this.startGame();
        } else if ("0".equals(choice)) {
            System.exit(0);
        } else {
            System.out.println("Invalid option.");
            displayInterface();
        }
    }


    public void displayGameInterface(char[][] field, char myMark) {
        System.out.println("\n--- Current Board ---");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Your mark: " + myMark);
        System.out.print("Enter move 'row col' (e.g., 1 2) or 'stop': ");

        String move = scanner.nextLine();
        if (move.equalsIgnoreCase("stop")) {
            System.exit(0);
        } else {
            this.controller.makeMove(move);
        }
    }


    public void printOnlyField(char[][] field) {
        System.out.println("\n--- Current Board (Waiting for opponent) ---");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }


    public void startGame() {
        System.out.println("Connecting to session...");
        this.controller.startGame();
    }


    public void showMessage(String message) {
        System.out.println("[SERVER]: " + message);
    }
}