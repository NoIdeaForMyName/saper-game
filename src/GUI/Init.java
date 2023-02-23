package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Init extends JFrame implements ActionListener {

    JPanel mainPanel = new JPanel();
    JLabel label = new JLabel("Choose level:");
    JPanel labelPanel = new JPanel();
    JButton easy = new JButton("Beginner");
    JButton medium = new JButton("Intermediate");
    JButton hard = new JButton("Expert");
    JPanel buttonsPanel = new JPanel();

    private final ImageIcon frame_icon = new ImageIcon("D:\\JAVA LABOLATORIA\\Cwiczenia 14.11.2022\\src\\resources\\icon.png");

    public Init() {

        easy.addActionListener(this);
        medium.addActionListener(this);
        hard.addActionListener(this);

        label.setFont(new Font("Arial", Font.PLAIN, 20));
        labelPanel.add(label);

        buttonsPanel.add(easy);
        buttonsPanel.add(medium);
        buttonsPanel.add(hard);

        buttonsPanel.setLayout(new GridLayout(1, 3, 1, 1));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(labelPanel);
        mainPanel.add(buttonsPanel);

        add(mainPanel);
        setSize(new Dimension(400, 120));
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Saper by Michal Maksanty");
        setIconImage(frame_icon.getImage());
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new Init();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int lvl;
        if (e.getSource() == easy) {
            lvl = 1;
        }
        else if (e.getSource() == medium) {
            lvl = 2;
        }
        else {
            lvl = 3;
        }
        dispose();
        try {
            new Game(lvl);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
