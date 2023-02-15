package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Init extends JFrame implements ActionListener {

    JPanel mainPanel = new JPanel();
    JLabel label = new JLabel("Choose level:");
    JPanel labelPanel = new JPanel();
    JButton easy = new JButton("Beginner");
    JButton medium = new JButton("Intermediate");
    JButton hard = new JButton("Expert");
    JPanel buttonsPanel = new JPanel();

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
        new Game2(lvl);
    }
}
