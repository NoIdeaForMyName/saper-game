package Saper;

import java.util.ArrayList;
public class Cell {

    private final ArrayList<Cell> neighbours = new ArrayList<>();
    private boolean bomb = false;
    private char show_content = 'f';
    private int surrounding_bombs = 0;

    public Cell() {}

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    public boolean getBomb() {
        return bomb;
    }

    public void setNeighbours(ArrayList<Cell> neighbours) {

        this.neighbours.addAll(neighbours);

        for (Cell cell : neighbours) {
            if (cell.getBomb())
                surrounding_bombs++;
        }
    }

    public int crossCell() {

        if (show_content != 'f')
            return 0;

        int crossed_cells = 1;

        if (bomb) {
            show_content = 'x';
            return -1;
        }

        else {

            show_content = 't';

            if (surrounding_bombs == 0) {
                for (Cell neighbour : neighbours) {
                    crossed_cells += neighbour.crossCell();
                }
            }
        }

        return crossed_cells;
    }

    public int rightClickCell() {
        if (show_content != 't') {
            if (show_content == 'f')
                show_content = 'p';
            else if (show_content == 'p')
                show_content = '?';
            else
                show_content = 'f';
            //show_content = (show_content == '?' ? 'f' : '?');
        }
        return 1;
    }

    public String getCellValue() {
        if (show_content == 't') {
            if (surrounding_bombs != 0)
                return String.valueOf(surrounding_bombs);
            else
                return "0";
        }
        else
            return String.valueOf(show_content);
    }

    public void uncover() {
        if (!bomb)
            show_content = 't';
        else
            show_content = 'x';
    }
}
