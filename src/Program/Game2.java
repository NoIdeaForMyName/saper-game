package Program;

import Saper.Board;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Objects;

// lvl 1: 8x8 10 bombs
// lvl 2: 16x16 40 bombs
// lvl 3: 30x16 99 bombs

// new Board, createBoard, displayBoard, gameLast, mark

public class Game2 extends JFrame {

    private Board board;
    private int m;
    private int n;
    private int bomb_nb;
    private boolean game_lasts;
    private boolean first_click = true;

    private JPanel statPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private ArrayList<ArrayList<JButton>> cells = new ArrayList<>();
    public Game2(int lvl) {
        switch (lvl) {
            case 1 -> {
                m = 8;
                n = 8;
                bomb_nb = 10;
            }
            case 2 -> {
                m = 16;
                n = 16;
                bomb_nb = 40;
            }
            case 3 -> {
                m = 16;
                n = 30;
                bomb_nb = 99;
            }
        }
        board = new Board(m, n, bomb_nb);
        game_lasts = true;

        System.out.println(m + "\n" + n);
        boardPanel.setLayout(new GridLayout(m, n, 1, 1));
        for (int i = 0; i < m; i++) {
            cells.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                cells.get(i).add(new JButton());

                int finalI = i;
                int finalJ = j;
                cells.get(i).get(j).addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {// left click
                            if (!first_click)
                                game_lasts = board.mark(finalI, finalJ, "x");
                            else {
                                board.createBoard(finalI, finalJ);
                                first_click = false;
                            }
                            if (!board.getBoard().get(finalI).get(finalJ).equals("p") & !board.getBoard().get(finalI).get(finalJ).equals("?"))
                                cells.get(finalI).get(finalJ).setEnabled(false);
                            refreshBoard();
                        }
                        else if (e.getButton() == MouseEvent.BUTTON3) // right click
                            board.mark(finalI, finalJ, "r");

                        cells.get(finalI).get(finalJ).setText(board.getBoard().get(finalI).get(finalJ));
                        System.out.println("ROW: " + finalI + "\nCOLUMN: " + finalJ);
                        System.out.println(board.getBoard().get(finalI).get(finalJ));
                    }
                });

                boardPanel.add(cells.get(i).get(j));
            }
        }

        boardPanel.setPreferredSize(new Dimension(400, 400));

        //vui test:
        add(statPanel);
        add(boardPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Saper by Michal Maksanty");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void refreshBoard() {
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                cells.get(i).get(j).setText(board.getBoard().get(i).get(j));
        if (!game_lasts) {
            System.out.println("YOU LOST!");
            board.uncoverAll();
            game_lasts = true;
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++)
                    cells.get(i).get(j).setEnabled(false);
            refreshBoard();
        }
    }

}
