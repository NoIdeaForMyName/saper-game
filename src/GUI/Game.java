package GUI;

import Minesweeper.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

// lvl 1: 8x8 10 bombs
// lvl 2: 16x16 40 bombs
// lvl 3: 30x16 99 bombs

public class Game extends JFrame {

    private Board board;
    private int m;
    private int n;
    private int bomb_nb;
    private boolean lost;
    private boolean first_click = true;
    private boolean clicked = false;

    private JPanel statsPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private JPanel borderBoardPanel;
    private JPanel borderStatsPanel;
    private CounterPanel bombCounterPanel;
    private CounterPanel timeCounterPanel;
    private JPanel mainPanel = new JPanel();
    private JButton emojiButton = new JButton();
    private ArrayList<ArrayList<MyButton>> cells = new ArrayList<>();
    private MyButton temp_button;
    private final int CELL_SIZE = 31;
    private final int EMOJI_SIZE = 51;
    private final int STATS_HEIGHT = 61;
    private final int BORDER_WIDTH = 19;
    private final int BOARD_WIDTH;
    private final int BOARD_HEIGHT;
    private int game_time;
    private Timer timer;

    private final Color bg_color = new Color(189, 189, 189);
    private final ImageIcon frame_icon = new ImageIcon("resources\\icon.png");
    private final ImageIcon[] clock_nb = new ImageIcon[11];
    private final ImageIcon[] numbers = new ImageIcon[9];
    private final ImageIcon[] symbols = new ImageIcon[7];
    private final ImageIcon[] emoji = new ImageIcon[5];
    private final ImageIcon[] borders = new ImageIcon[8];

    public Game(int lvl) throws IOException {

        timer = new Timer(1000, e -> {
            game_time++;
            if (game_time == 1000)
                game_time = 0;
            timeCounterPanel.number = game_time;
            timeCounterPanel.repaint();
        });

        initialize_sprite();

        // initiating board
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

        BOARD_WIDTH = n * CELL_SIZE;
        BOARD_HEIGHT = m * CELL_SIZE;

        board = new Board(m, n, bomb_nb);
        lost = false;

        boardPanel.setLayout(new GridLayout(m, n, 0, 0));

        for (int i = 0; i < m; i++) {
            cells.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                cells.get(i).add(new MyButton(symbols[0], i, j));
                cells.get(i).get(j).addMouseListener(new ButtonListener());
                boardPanel.add(cells.get(i).get(j));
            }
        }

        boardPanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        //add borders to boardPanel...
        borderBoardPanel = new BBPanel(BOARD_WIDTH, BOARD_HEIGHT);
        borderBoardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 19, 19));
        borderBoardPanel.add(boardPanel);
        //end of adding...

        emojiButton.addMouseListener(new EmojiListener());
        emojiButton.setPreferredSize(new Dimension(EMOJI_SIZE, EMOJI_SIZE));
        emojiButton.setIcon(emoji[0]);
        emojiButton.setRolloverEnabled(false);

        bombCounterPanel = new CounterPanel(bomb_nb);
        timeCounterPanel = new CounterPanel(game_time);

        //centering everything in statsPanel...
        JPanel emojiWrapperPanel = new JPanel();
        emojiWrapperPanel.add(emojiButton);
        emojiWrapperPanel.setBackground(bg_color);

        JPanel bombWrapperPanel = new JPanel();
        bombWrapperPanel.setLayout(new BoxLayout(bombWrapperPanel, BoxLayout.Y_AXIS));
        bombWrapperPanel.add(Box.createVerticalGlue());
        bombWrapperPanel.add(bombCounterPanel);
        bombWrapperPanel.setBackground(bg_color);

        JPanel timeWrapperPanel = new JPanel();
        timeWrapperPanel.setLayout(new BoxLayout(timeWrapperPanel, BoxLayout.Y_AXIS));
        timeWrapperPanel.add(Box.createVerticalGlue());
        timeWrapperPanel.add(timeCounterPanel);
        timeWrapperPanel.setBackground(bg_color);

        statsPanel.setPreferredSize(new Dimension(BOARD_WIDTH - 20, STATS_HEIGHT)); //20 - gap between side components and borders of statsPanel
        statsPanel.setLayout(new BorderLayout());

        statsPanel.add(BorderLayout.WEST, bombWrapperPanel);
        statsPanel.add(BorderLayout.CENTER, emojiWrapperPanel);
        statsPanel.add(BorderLayout.EAST, timeWrapperPanel);
        //end centering...

        //add borders to statsPanel...
        borderStatsPanel = new BSPanel(BOARD_WIDTH, STATS_HEIGHT);
        borderStatsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, (BOARD_WIDTH+2*BORDER_WIDTH-EMOJI_SIZE)/2, BORDER_WIDTH));
        borderStatsPanel.add(statsPanel);
        //end adding...

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(borderStatsPanel);
        mainPanel.add(borderBoardPanel);

        //Frame Initialization:
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Minesweeper");
        setIconImage(frame_icon.getImage());
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void refreshBoard() {

        if (!board.gameLast() | lost) { //game is over...
            timer.stop();

            if (board.gameLast()) {// if you have lost:
                board.uncoverAll();
                emojiButton.setIcon(emoji[3]);
            }
            else {// if you have won:
                emojiButton.setIcon(emoji[4]);
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++)
                        if (board.getBoard().get(i).get(j).equals("f"))
                            board.mark(i, j, "r");
                        else if (board.getBoard().get(i).get(j).equals("?")) {
                            board.mark(i, j, "r");
                            board.mark(i, j, "r");
                        }
                }
                bombCounterPanel.number = 0;
                bombCounterPanel.repaint();
            }
        }

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                setCellIcon(cells.get(i).get(j));
            }
    }

    public void setCellIcon(MyButton button) { // p-flag; f-covered; ?-?; nb-nb; x-bomb WROC TO

        String board_cell;
        if (!first_click)
            board_cell = board.getBoard().get(button.i).get(button.j);
        else
            board_cell = "f";

        switch (board_cell) {

            case "p" -> button.setIcon(symbols[1]); //button.setDisabledIcon(symbols[1]);

            case "f" -> {
                if (!clicked)
                    button.setIcon(symbols[0]); //button.setDisabledIcon(symbols[0]);
                else
                    button.setIcon(numbers[0]);

            }
            case "?" -> button.setIcon((symbols[5])); //button.setDisabledIcon(symbols[5]);

            case "x" -> button.setIcon(symbols[4]); //button.setDisabledIcon(symbols[4]);

            case "w" -> button.setIcon(symbols[3]); //button.setDisabledIcon(symbols[3]);

            case "z" -> button.setIcon(symbols[2]); //button.setDisabledIcon(symbols[2]);

            default -> button.setIcon(numbers[Integer.parseInt(board_cell)]); //button.setDisabledIcon(numbers[Integer.parseInt(board_cell)]);

        }

    }

    public void initialize_sprite() throws IOException {

        BufferedImage sprite = ImageIO.read(new File("resources\\sprite.png"));

        int x_pos = 0;
        int y_pos = 0;
        int size_x = 25;
        int size_y = 45;
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
    }

    private class MyButton extends JButton {

        private final int i;
        private final int j;
        private MyButton(ImageIcon icon, int i, int j) {
            setIcon(icon);
            setRolloverEnabled(false);
            this.i = i;
            this.j = j;
        }
    }

    private class ButtonListener implements MouseListener {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (board.gameLast()) {
                clicked = false;
                emojiButton.setIcon(emoji[0]);
                if (temp_button != null) {
                    if (e.getButton() == MouseEvent.BUTTON1) {// left click
                        if (!first_click)
                            lost = !board.mark(temp_button.i, temp_button.j, "x");
                        else {
                            board.createBoard(temp_button.i, temp_button.j);
                            first_click = false;
                            game_time = -1;
                            timer.setInitialDelay(0);
                            timer.start();
                        }
                        refreshBoard();
                    } else if (e.getButton() == MouseEvent.BUTTON3 & !first_click) { // right click
                        board.mark(temp_button.i, temp_button.j, "r");
                        setCellIcon(temp_button);
                        bombCounterPanel.number = (bomb_nb - board.getFlagged());
                        bombCounterPanel.repaint();
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            temp_button = (MyButton) e.getSource();
            if (clicked)
                setCellIcon(temp_button);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (clicked) {
                clicked = false;
                setCellIcon(temp_button);
                clicked = true;
            }
            temp_button = null;
        }
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 & board.gameLast()) {
                clicked = true;
                //set temp_button icon on clicked
                setCellIcon(temp_button);
                emojiButton.setIcon(emoji[2]);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

    }

    private class EmojiListener implements MouseListener {

        private boolean pressed = false;
        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            emojiButton.setIcon(emoji[1]);
            pressed = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (emojiButton.getIcon() == emoji[1]) {
                //here the game will be reset:
                board = new Board(m, n, bomb_nb);
                lost = false;
                first_click = true;
                bombCounterPanel.number = bomb_nb;
                bombCounterPanel.repaint();
                timer.stop();
                timeCounterPanel.number = 0;
                timeCounterPanel.repaint();
                //cover all
                for (int i = 0; i < m; i++)
                    for (int j = 0; j < n; j++)
                        setCellIcon(cells.get(i).get(j));

                //end cover all
            }
            emojiButton.setIcon(emoji[0]);
            pressed = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (pressed)
                emojiButton.setIcon(emoji[1]);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (pressed)
                emojiButton.setIcon(emoji[0]);
        }
    }

    private class BBPanel extends JPanel { // Border Board Panel

        private final int B_WIDTH;
        private final int B_HEIGHT;

        private BBPanel(int dim_x, int dim_y) {
            setPreferredSize(new Dimension(dim_x + 2*BORDER_WIDTH, dim_y + 2*BORDER_WIDTH));
            B_WIDTH = dim_x;
            B_HEIGHT = dim_y;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

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

    private class BSPanel extends JPanel { // Border Stats Panel

        private final int S_WIDTH;
        private final int S_HEIGHT;

        private BSPanel(int dim_x, int dim_y) {
            setBackground(bg_color);
            setPreferredSize(new Dimension(dim_x + 2*BORDER_WIDTH, dim_y + BORDER_WIDTH));
            S_WIDTH = dim_x;
            S_HEIGHT = dim_y;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

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

    private class CounterPanel extends JPanel {

        private final int DIGIT_WIDTH = 25;
        private final int DIGIT_HEIGHT = 45;
        private int number;

        private CounterPanel(int number) {
            setPreferredSize(new Dimension(DIGIT_WIDTH * 3, DIGIT_HEIGHT));
            setBackground(bg_color);
            this.number = number;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            LinkedList<String> number_list = new LinkedList<>(Arrays.asList(Integer.toString(number).split("")));
            while (number_list.size() != 3) {
                if (number_list.getFirst().equals("-")) {
                    number_list.set(0, "0");
                    number_list.addFirst("-");
                }
                else
                    number_list.addFirst("0");
            }
            for (int i = 0; i < number_list.size(); i++) {
                String index = number_list.get(i);
                if (index.equals("-"))
                    index = "10";
                g.drawImage(clock_nb[Integer.parseInt(index)].getImage(), i * DIGIT_WIDTH, 0, null);
            }
        }
    }

}
