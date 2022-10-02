import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Board extends JPanel implements ActionListener, MouseListener {

    JButton newGameButton;
    JButton resignButton;
    JLabel message;

    private Data board;

    private boolean Active;

    private Data.Check currentPlayer;

    private int selectedRow, selectedCol;

    private Move[] legalMoves;

    Board() {
        setBackground(Color.BLACK);
        addMouseListener(this);
        resignButton = new JButton("Сдаться");
        resignButton.addActionListener(this);
        newGameButton = new JButton("Новая игра");
        newGameButton.addActionListener(this);
        message = new JLabel("",JLabel.CENTER);
        message.setFont(new  Font("Serif", Font.BOLD, 14));
        message.setForeground(Color.green);
        board = new Data();
        newGame();
    }

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == newGameButton)
            newGame();
        else if (src == resignButton)
            resign();
    }

    private void newGame() {
        board.setUp();
        currentPlayer = Data.Check.WHITE;
        legalMoves = board.getMoves(Data.Check.WHITE);
        selectedRow = -1;
        message.setText("Ход белых");
        Active = true;
        newGameButton.setEnabled(false);
        resignButton.setEnabled(true);
        repaint();
    }

    private void resign() {
        if (currentPlayer == Data.Check.WHITE)
            gameOver("Белые сдались");
        else
            gameOver("Чёрные сдались");
    }

    private void gameOver(String str) {
        message.setText(str);
        newGameButton.setEnabled(true);
        resignButton.setEnabled(false);
        Active = false;
    }

    private void clickSquare(int row, int col) {

        for (Move move : legalMoves)
            if (move.row1 == row && move.col1 == col) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer == Data.Check.WHITE)
                    message.setText("Ход белых");
                else
                    message.setText("Ход чёрных");
                repaint();
                return;
            }

        if (selectedRow < 0) {
            message.setText("Нажми на шашку, которую хочешь передвинуть");
            return;
        }

        for (Move legalMove : legalMoves)
            if (legalMove.row1 == selectedRow && legalMove.col1 == selectedCol
                    && legalMove.row2 == row && legalMove.col2 == col) {
                doMakeMove(legalMove);
                return;
            }

        message.setText("Нажми на поле, куда хочешь передвинуть шашку");

    }

    private void doMakeMove(Move move) {

        board.makeMove(move);

        if (move.isJump()) {
            legalMoves = board.getJumps(currentPlayer,move.row2,move.col2);
            if (legalMoves != null) {
                if (currentPlayer == Data.Check.WHITE)
                    message.setText("Белые должны бить дальше");
                else
                    message.setText("Чёрные должны бить дальше");
                selectedRow = move.row2;
                selectedCol = move.col2;
                repaint();
                return;
            }
        }

        if (currentPlayer == Data.Check.WHITE) {
            currentPlayer = Data.Check.BLACK;
            legalMoves = board.getMoves(currentPlayer);
            if (legalMoves == null){
                gameOver("Победа белых");
                repaint();
                return;
            }
            else if (legalMoves[0].isJump())
                message.setText("Чёрный бить обязан");
            else
                message.setText("Ход чёрных");
        }
        else {
            currentPlayer = Data.Check.WHITE;
            legalMoves = board.getMoves(currentPlayer);
            if (legalMoves == null){
                gameOver("Победа чёрных");
                repaint();
                return;
            }
            else if (legalMoves[0].isJump())
                message.setText("Белый бить обязан");
            else
                message.setText("Ход белых");
        }

        selectedRow = -1;

        if (legalMoves.length == 1) {
            selectedRow = legalMoves[0].row1;
            selectedCol = legalMoves[0].col1;
        }

        repaint();

    }

    public void paintComponent(Graphics g) {

        g.setColor(Color.black);
        g.drawRect(0,0,getSize().width-1,getSize().height-1);
        g.drawRect(1,1,getSize().width-3,getSize().height-3);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 == col % 2 )
                    g.setColor(Color.LIGHT_GRAY);
                else
                    g.setColor(Color.GRAY);
                g.fillRect(2 + col*40, 2 + row*40, 40, 40);
                switch (board.pieceAt(row,col)) {
                    case WHITE:
                        g.setColor(Color.WHITE);
                        g.fillOval(5 + col*40, 5 + row*40, 34, 34);
                        break;
                    case BLACK:
                        g.setColor(Color.BLACK);
                        g.fillOval(5 + col*40, 5 + row*40, 34, 34);
                        break;
                    case WHITEQ:
                        g.setColor(Color.WHITE);
                        g.fillOval(5 + col*40, 5 + row*40, 34, 34);
                        g.setColor(Color.BLACK);
                        g.drawString("Q", 16 + col*40, 26 + row*40);
                        break;
                    case BLACKQ:
                        g.setColor(Color.BLACK);
                        g.fillOval(5 + col*40, 5 + row*40, 34, 34);
                        g.setColor(Color.WHITE);
                        g.drawString("Q", 16 + col*40, 26 + row*40);
                        break;
                }
            }
        }

        if (Active) {
            g.setColor(Color.cyan);
            for (Move legalMove : legalMoves) {
                g.drawRect(2 + legalMove.col1 * 40, 2 + legalMove.row1 * 40, 39, 39);
                g.drawRect(3 + legalMove.col1 * 40, 3 + legalMove.row1 * 40, 37, 37);
            }
            if (selectedRow >= 0) {
                g.setColor(Color.white);
                g.drawRect(2 + selectedCol*40, 2 + selectedRow*40, 39, 39);
                g.drawRect(3 + selectedCol*40, 3 + selectedRow*40, 37, 37);
                g.setColor(Color.green);
                for (Move legalMove : legalMoves) {
                    if (legalMove.col1 == selectedCol && legalMove.row1 == selectedRow) {
                        g.drawRect(2 + legalMove.col2 * 40, 2 + legalMove.row2 * 40, 39, 39);
                        g.drawRect(3 + legalMove.col2 * 40, 3 + legalMove.row2 * 40, 37, 37);
                    }
                }
            }
        }
    }

    public void mousePressed(MouseEvent evt) {
        if (!Active)
            message.setText("Начни новую игру");
        else {
            int col = (evt.getX() - 2) / 40;
            int row = (evt.getY() - 2) / 40;
            if (col >= 0 && col < 8 && row >= 0 && row < 8)
                clickSquare(row, col);
        }
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

}