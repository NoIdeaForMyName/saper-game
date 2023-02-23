package Saper;

import java.util.ArrayList;
import java.util.Random;

public class Board {

    private final ArrayList<ArrayList<Cell>> board = new ArrayList<>();
    private final Random random = new Random();
    private final int m;
    private final int n;
    private final int bomb_nb;
    private int to_cross_nb;
    private int flagged = 0;

    public Board(int m, int n, int bomb_nb) {

        this.m = m;
        this.n = n;
        this.bomb_nb = bomb_nb;
        to_cross_nb = m*n - bomb_nb;
    }

    public void createBoard(int x, int y) {

        for (int i = 0; i < m; i++) {
            board.add(new ArrayList<>());
            for (int j = 0; j < n; j++)
                board.get(i).add(new Cell());
        }

        int temp_bomb_nb = bomb_nb;
        while (temp_bomb_nb > 0) {

            int i = random.nextInt(0, m);
            int j = random.nextInt(0, n);
            if (!(i == x && j == y) && !board.get(i).get(j).getBomb()) {
                board.get(i).get(j).setBomb(true);
                temp_bomb_nb--;
            }
        }
        assignValues();
        mark(x, y, "x");
    }

    public void assignValues() {

        ArrayList<Cell> neighbours = new ArrayList<>();

        for (int i = 0; i < m; i++) {

            for (int j = 0; j < n; j++) {

                for (int x = i-1; x < i+2; x++) {

                    if (x < 0 | x >= m)
                        continue;

                    for (int y = j-1; y < j+2; y++) {

                        if (y < 0 | y >= n)
                            continue;

                        if (!(i == x && j == y))
                            neighbours.add(board.get(x).get(y));
                    }
                }

                board.get(i).get(j).setNeighbours(neighbours);
                neighbours.clear();
            }

        }

    }

    public boolean mark(int x, int y, String marking_type) {

        if (marking_type.equals("x")) {

            int temp = board.get(x).get(y).crossCell();
            to_cross_nb -= temp;

            return temp >= 0;
        }

        else {
            String content = board.get(x).get(y).rightClickCell();
            if (content.equals("p"))
                flagged++;
            else if (content.equals("?"))
                flagged--;
            return true;
        }
    }

    public ArrayList<ArrayList<String>> getBoard() {

        ArrayList<ArrayList<String>> b = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            b.add(new ArrayList<>());

            for(int j = 0; j < n; j++) {
                b.get(i).add(board.get(i).get(j).getCellValue());
            }

        }
        return b;
    }

    public int getFlagged() {
        return flagged;
    }

    public boolean gameLast() {
        return to_cross_nb > 0;
    }

    public void uncoverAll() {

        for (ArrayList<Cell> list : board)
            for (Cell cell : list)
                cell.uncover();
    }

}
