package Program;

import Saper.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// lvl 1: 8x8 10 bombs
// lvl 2: 16x16 40 bombs
// lvl 3: 30x16 99 bombs

// new Board, createBoard, displayBoard, gameLast, mark

public class Game extends JFrame {

    private Board board;
    private int m;
    private int n;
    private int bomb_nb;
    private boolean won;
    private boolean first_click = true;

    private JPanel statsPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private JPanel borderBoardPanel;
    private JPanel borderStatsPanel;
    private JPanel mainPanel = new JPanel();
    private JButton emojiButton = new JButton();
    private ArrayList<ArrayList<JButton>> cells = new ArrayList<>();
    private JButton[] temp_button = new JButton[1];
    private final int CELL_SIZE = 31; // 30 is good
    private final int EMOJI_SIZE = 51;
    private final int STATS_HEIGHT = 61;
    private final int BORDER_WIDTH = 19; // 20
    private int BOARD_WIDTH;
    private int BOARD_HEIGHT;

    private final ImageIcon[] clock_nb = new ImageIcon[11];
    private final ImageIcon[] numbers = new ImageIcon[9];
    private final ImageIcon[] symbols = new ImageIcon[7];
    private final ImageIcon[] emoji = new ImageIcon[5];
    private final ImageIcon[] borders = new ImageIcon[8];
    public Game(int lvl) throws IOException {

        //sprite:
        BufferedImage sprite = ImageIO.read(new File("D:\\JAVA LABOLATORIA\\Cwiczenia 14.11.2022\\src\\resources\\sprite.png"));

        int x_pos = 0;
        int y_pos = 0; // 46
        int size_x = 25; // 31 31 51
        int size_y = 45; // 31
        ImageIcon[] temp = clock_nb;

        for (int row = 0; row < 5; row++) {
            switch (row) {
                case 0 -> {}
                case 1 -> {
                    x_pos = 0;
                    y_pos = 46;
                    size_x = 31;
                    size_y = 31;
                    temp = numbers;
                }
                case 2 -> {
                    x_pos = 0;
                    y_pos = 78;
                    size_x = 31;
                    size_y = 31;
                    temp = symbols;
                }
                case 3 -> {
                    x_pos = 0;
                    y_pos = 110;
                    size_x = 51;
                    size_y = 51;
                    temp = emoji;
                }
                case 4 -> {
                    x_pos = 268;
                    y_pos = 78;
                    size_x = 19;
                    size_y = 63;
                    temp = borders;
                }
            }
            for (int i = 0; i < temp.length; i++) {

                if (temp == borders & i != 0) {

                    size_x = 19;

                    x_pos = (size_x + 1) * (i - 1);
                    if (i > 5)
                        x_pos += 12;


                    if (i == 5) {
                        size_x = 31;
                    }

                    y_pos = 162;
                    size_y = 19;
                }

                BufferedImage image = new BufferedImage(size_x, size_y, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = image.createGraphics();
                g.drawImage(sprite, 0, 0, size_x, size_y, x_pos, y_pos, x_pos + size_x, y_pos + size_y, null);
                g.dispose();
                temp[i] = new ImageIcon(image);
                x_pos += size_x + 1;
            }
        }
        // end sprite

        // initiating board
        switch (lvl) {
            case 1 -> {
                m = 8;
                n = 8;
                bomb_nb = 3; // 10
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

        BOARD_WIDTH = n * CELL_SIZE;
        BOARD_HEIGHT = m * CELL_SIZE;

        board = new Board(m, n, bomb_nb);
        won = true;

        boardPanel.setLayout(new GridLayout(m, n, 0, 0));

        for (int i = 0; i < m; i++) {
            cells.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                cells.get(i).add(new JButton(symbols[0]));
                cells.get(i).get(j).setRolloverEnabled(false); //backlight disabled

                int finalI = i;
                int finalJ = j;
                cells.get(i).get(j).addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (temp_button[0] == e.getSource()) {
                            if (e.getButton() == MouseEvent.BUTTON1) {// left click
                                if (!first_click)
                                    won = board.mark(finalI, finalJ, "x");
                                else {
                                    board.createBoard(finalI, finalJ);
                                    first_click = false;
                                }
                                refreshBoard();
                            } else if (e.getButton() == MouseEvent.BUTTON3) // right click
                                board.mark(finalI, finalJ, "r");

                            //((JButton) e.getSource()).setText(board.getBoard().get(finalI).get(finalJ));
                            setCellIcon((JButton) e.getSource(), board.getBoard().get(finalI).get(finalJ));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        temp_button[0] = null;
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        temp_button[0] = (JButton) e.getSource();
                    }

                });

                boardPanel.add(cells.get(i).get(j));
            }
        }

        boardPanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        //add borders to boardPanel...
        borderBoardPanel = new BBPanel(BOARD_WIDTH, BOARD_HEIGHT);
        borderBoardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 19, 19));
        borderBoardPanel.add(boardPanel);
        //end of adding...

        emojiButton.setPreferredSize(new Dimension(EMOJI_SIZE, EMOJI_SIZE));
        emojiButton.setIcon(emoji[0]);

        //add borders to statsPanel...
        borderStatsPanel = new BSPanel(BOARD_WIDTH, STATS_HEIGHT);
        borderStatsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, (BOARD_WIDTH+2*BORDER_WIDTH-EMOJI_SIZE)/2, (STATS_HEIGHT+2*BORDER_WIDTH-EMOJI_SIZE)/2));
        borderStatsPanel.add(emojiButton);
        //end adding...

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(borderStatsPanel);
        mainPanel.add(borderBoardPanel);

        //Frame Initialization:
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Saper by Michal Maksanty");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void refreshBoard() {

        if (!board.gameLast() | !won) {
            System.out.println("GAME OVER!");

            if (board.gameLast()) {// if you have lost:
                System.out.println("YOU LOST!");
                board.uncoverAll();
//                for (int i = 0; i < m; i++)
//                    for (int j = 0; j < n; j++)
//                        cells.get(i).get(j).setEnabled(false);
            }
            else
                System.out.println("YOU WON!");
            //won = true;
            //refreshBoard();

            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++)
                    //cells.get(i).get(j).setEnabled(false);
                    System.out.println("odkomentuj to u gory!!!");
        }

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                String temp_board_cell = board.getBoard().get(i).get(j);
                setCellIcon(cells.get(i).get(j), temp_board_cell);
                if (!temp_board_cell.equals("p") & !temp_board_cell.equals("?") & !temp_board_cell.equals("f"))
                    //cells.get(i).get(j).setEnabled(false); //false !!!
                    System.out.println("odkomentuj to u gory!!!"); //nalezy cos pozmieniac w MouseReleased bo jest problem z tym ze on i tak ma gdzies czy przycisk jest disabled czy nie
            }

    }

    public void setCellIcon(JButton button, String board_cell) { // p-flag; f-covered; ?-?; nb-nb; x-bomb

        switch (board_cell) {
            case "p" -> {
                button.setIcon(symbols[1]);
                button.setDisabledIcon(symbols[1]);
            }
            case "f" -> {
                button.setIcon(symbols[0]);
                button.setDisabledIcon(symbols[0]);
            }
            case "?" -> {
                button.setIcon((symbols[5]));
                button.setDisabledIcon(symbols[5]);
            }
            case "x" -> {
                button.setIcon(symbols[4]);
                button.setDisabledIcon(symbols[4]);
            }
            case "w" -> {
                button.setIcon(symbols[3]);
                button.setDisabledIcon(symbols[3]);
            }
            case "z" -> {
                button.setIcon(symbols[2]);
                button.setDisabledIcon(symbols[2]);
            }
            default -> {
                button.setIcon(numbers[Integer.parseInt(board_cell)]);
                button.setDisabledIcon(numbers[Integer.parseInt(board_cell)]);
            }
        }

    }

    class BBPanel extends JPanel {

        private int B_WIDTH;
        private int B_HEIGHT;

        private BBPanel(int dim_x, int dim_y) {
            setPreferredSize(new Dimension(dim_x + 2*BORDER_WIDTH, dim_y + 2*BORDER_WIDTH));
            B_WIDTH = dim_x;
            B_HEIGHT = dim_y;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // g.drawImage(image, 0, 0, null);
            // borders[i] 0-pionowa 1-LG rog 2-PG rog 3-LD rog 4-PD rog 5-pozioma 6-L lacznik 7-P lacznik
            int amount_hor = (int) Math.ceil(((double) B_WIDTH)/borders[5].getIconWidth());
            int amount_ver = (int) Math.ceil(((double) B_HEIGHT)/borders[0].getIconHeight());

            g.drawImage(borders[6].getImage(), 0, 0, null);
            g.drawImage(borders[7].getImage(), B_WIDTH + BORDER_WIDTH, 0, null);
            for (int i = 0; i < amount_hor; i++) {
                g.drawImage(borders[5].getImage(), i*(borders[5].getIconWidth()) + BORDER_WIDTH, 0, null);
                g.drawImage(borders[5].getImage(), i*(borders[5].getIconWidth()) + BORDER_WIDTH, B_HEIGHT + BORDER_WIDTH, null);
            }

            g.drawImage(borders[3].getImage(), 0, BOARD_HEIGHT + BORDER_WIDTH, null);
            g.drawImage(borders[4].getImage(), BOARD_WIDTH + BORDER_WIDTH, BOARD_HEIGHT + BORDER_WIDTH, null);
            for (int i = 0; i < amount_ver; i++) {
                g.drawImage(borders[0].getImage(), 0, i*(borders[0].getIconHeight()) + BORDER_WIDTH, null);
                g.drawImage(borders[0].getImage(), BOARD_WIDTH + BORDER_WIDTH, i*(borders[0].getIconHeight()) + BORDER_WIDTH, null);
            }

        }
    }

    class BSPanel extends JPanel {

        private int S_WIDTH;
        private int S_HEIGHT;

        private BSPanel(int dim_x, int dim_y) {
            setPreferredSize(new Dimension(dim_x + 2*BORDER_WIDTH, dim_y + BORDER_WIDTH));
            S_WIDTH = dim_x;
            S_HEIGHT = dim_y;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // g.drawImage(image, 0, 0, null);
            // borders[i] 0-pionowa 1-LG rog 2-PG rog 3-LD rog 4-PD rog 5-pozioma 6-L lacznik 7-P lacznik
            int amount_hor = (int) Math.ceil(((double) S_WIDTH)/borders[5].getIconWidth());
            int amount_ver = (int) Math.ceil(((double) S_HEIGHT)/borders[0].getIconHeight());

            g.drawImage(borders[1].getImage(), 0, 0, null);
            g.drawImage(borders[2].getImage(), S_WIDTH + BORDER_WIDTH, 0, null);
            for (int i = 0; i < amount_hor; i++)
                g.drawImage(borders[5].getImage(), i*(borders[5].getIconWidth()) + BORDER_WIDTH, 0, null);

            for (int i = 0; i < amount_ver; i++) {
                g.drawImage(borders[0].getImage(), 0, i*(borders[0].getIconHeight()) + BORDER_WIDTH, null);
                g.drawImage(borders[0].getImage(), BOARD_WIDTH + BORDER_WIDTH, i*(borders[0].getIconHeight()) + BORDER_WIDTH, null);
            }

        }

    }

}
