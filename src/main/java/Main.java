import java.awt.*;
import javax.swing.*;


public class Main extends JPanel {

    private Main() {

        setLayout(null);
        setPreferredSize(new Dimension(350,450));

        setBackground(new Color(0,150,0));

        Board board = new Board();
        add(board);
        add(board.newGameButton);
        add(board.resignButton);
        add(board.message);

        board.setBounds(20,20,324,324);
        board.newGameButton.setBounds(20, 400, 120, 30);
        board.resignButton.setBounds(224, 400, 120, 30);
        board.message.setBounds(0, 350, 350, 30);
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Шашки");
        Main content = new Main();
        window.setContentPane(content);
        window.pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation( (screensize.width - window.getWidth())/2,
                (screensize.height - window.getHeight())/2 );
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);
    }

}