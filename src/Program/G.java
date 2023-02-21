package Program;

import Saper.Board;

import java.util.ArrayList;
import java.util.Scanner;

public class G {

    private static boolean invalidInput(String [] cord, int max_m, int max_n, boolean is_first) {

        if (is_first) {
            if (cord.length != 2)
                return true;
        }
        else {
            if (cord.length != 3)
                return true;
        }

        if (isNaturalDigit(cord[0]) && Integer.parseInt(cord[0])<max_n &&
                isNaturalDigit(cord[1]) && Integer.parseInt(cord[1])<max_m) {
            if (is_first)
                return false;

            else
                return !(cord[2].equals("x") | cord[2].equals("f") | cord[2].equals("?"));
        }
        else
            return true;

    }

    private static boolean isNaturalDigit(String s) {

        try {
            return Integer.parseInt(s) >= 0;

        } catch (NumberFormatException ex) {
            return false;
        }

    }

    private static void displayBoard(Board board) {

        ArrayList<ArrayList<String>> b = board.getBoard();

        System.out.print("   ");
        for (int i = 0; i < b.get(0).size(); i++)
            System.out.printf("%-3d", i);

        for (int i = 0; i < b.size(); i++) {

            System.out.println();
            System.out.printf("%-2d", i);
            System.out.print("[");

            for (int j = 0; j < b.get(0).size()-1; j++) {

                if (b.get(i).get(j).equals("■") | b.get(i).get(j).equals("□") | b.get(i).get(j).equals("⚑"))
                    System.out.printf("%-5s", b.get(i).get(j));

                else
                    System.out.printf("%-2s %s", b.get(i).get(j), "");
            }

            System.out.print(b.get(i).get(b.get(i).size()-1) + "]");
        }
        System.out.println("\n");
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String [] board_parameters = new String[0];
        boolean valid_input = false;

        int n;
        int m;

        while (!valid_input) {

            System.out.println("Separate the data with whitespaces, e.g. \"10 10 25\"");
            System.out.print("Enter board dimensions and the number of bombs... ");
            board_parameters = scanner.nextLine().split("\s+");

            if (board_parameters.length == 3) {

                valid_input = true;

                for (String s : board_parameters) {

                    if (!isNaturalDigit(s))
                        valid_input = false;
                }
            }

            if (valid_input) {

                n = Integer.parseInt(board_parameters[0]);
                m = Integer.parseInt(board_parameters[1]);

                if (n*m <= Integer.parseInt(board_parameters[2]))
                    valid_input = false;
            }

            if (!valid_input)
                System.out.println("Incorrect data!");
        }

        n = Integer.parseInt(board_parameters[0]);
        m = Integer.parseInt(board_parameters[1]);

        Board board = new Board(m, n, Integer.parseInt(board_parameters[2]));

        System.out.print("Enter coordinates for the first cell (e.g. \"4 4\")... ");
        String [] coordinates = scanner.nextLine().split("\s+");

        while (invalidInput(coordinates, m, n, true)) {

            System.out.println("Invalid data!");
            System.out.print("Enter coordinates... ");
            coordinates = scanner.nextLine().split("\s+");
        }

        boolean won = true;

        board.createBoard(Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[0]));

        displayBoard(board);

        System.out.println("actions:\nx - cross cell\nf - flag cell\n? - mark cell as unknown");
        System.out.println("Enter coordinates and the type of action (e.g. \"3 2 x\")");

        while(board.gameLast() && won) {

            System.out.print("Enter data... ");
            coordinates = scanner.nextLine().split("\s+");

            while (invalidInput(coordinates, m, n, false)) {

                System.out.println("Invalid data!");
                System.out.print("Enter coordinates and the type of action... ");
                coordinates = scanner.nextLine().split("\s+");
            }

            if (!board.mark(Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[0]), coordinates[2])) {
                won = false;
            }

            displayBoard(board);
        }

        System.out.println("Game over!");
        if (won)
            System.out.println("Congratulations! You won!");
        else
            System.out.println("Unfortunately, you lost!");

        System.out.println("Here is the whole uncovered board:");
        board.uncoverAll();
        displayBoard(board);
    }
}
