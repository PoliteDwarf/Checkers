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


    private CheckersData board;

    private boolean gameInProgress;

    private int currentPlayer;

    private int selectedRow, selectedCol;

    private CheckersMove[] legalMoves;

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
        board = new CheckersData();
        doNewGame();
    }

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == newGameButton)
            doNewGame();
        else if (src == resignButton)
            doResign();
    }

    private void doNewGame() {
        board.setUpGame();
        currentPlayer = CheckersData.WHITE;
        legalMoves = board.getLegalMoves(CheckersData.WHITE);
        selectedRow = -1;
        message.setText("Ход белых");
        gameInProgress = true;
        newGameButton.setEnabled(false);
        resignButton.setEnabled(true);
        repaint();
    }

    private void doResign() {
        if (currentPlayer == CheckersData.WHITE)
            gameOver("Белые сдались");
        else
            gameOver("Чёрные сдались");
    }

    private void gameOver(String str) {
        message.setText(str);
        newGameButton.setEnabled(true);
        resignButton.setEnabled(false);
        gameInProgress = false;
    }

    private void doClickSquare(int row, int col) {

        for (CheckersMove move : legalMoves)
            if (move.fromRow == row && move.fromCol == col) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer == CheckersData.WHITE)
                    message.setText("Ход белых");
                else
                    message.setText("Ход чёрных");
                repaint();
                return;
            }

        if (selectedRow < 0) {
            message.setText("Нажми на шашку, которую хочешь подвинуть");
            return;
        }

        for (CheckersMove legalMove : legalMoves)
            if (legalMove.fromRow == selectedRow && legalMove.fromCol == selectedCol
                    && legalMove.toRow == row && legalMove.toCol == col) {
                doMakeMove(legalMove);
                return;
            }

        message.setText("Нажми на поле, куда хочешь передвинуть шашку");

    }

    private void doMakeMove(CheckersMove move) {

        board.makeMove(move);

        if (move.isJump()) {
            legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
            if (legalMoves != null) {
                if (currentPlayer == CheckersData.WHITE)
                    message.setText("Белые должны бить дальше");
                else
                    message.setText("Чёрные должны бить дальше");
                selectedRow = move.toRow;
                selectedCol = move.toCol;
                repaint();
                return;
            }
        }

        if (currentPlayer == CheckersData.WHITE) {
            currentPlayer = CheckersData.BLACK;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver("Победа белых");
            else if (legalMoves[0].isJump())
                message.setText("Чёрный бить обязан");
            else
                message.setText("Ход чёрных");
        }
        else {
            currentPlayer = CheckersData.WHITE;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver("Победа чёрных");
            else if (legalMoves[0].isJump())
                message.setText("Белый бить обязан");
            else
                message.setText("Ход белых");
        }

        selectedRow = -1;

        if (legalMoves != null) {
            boolean sameStartSquare = true;
            for (int i = 1; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow != legalMoves[0].fromRow
                        || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                    sameStartSquare = false;
                    break;
                }
            if (sameStartSquare) {
                selectedRow = legalMoves[0].fromRow;
                selectedCol = legalMoves[0].fromCol;
            }
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
                    case CheckersData.WHITE:
                        g.setColor(Color.WHITE);
                        g.fillOval(4 + col*40, 4 + row*40, 35, 35);
                        break;
                    case CheckersData.BLACK:
                        g.setColor(Color.BLACK);
                        g.fillOval(4 + col*40, 4 + row*40, 35, 35);
                        break;
                    case CheckersData.WHITE_KING:
                        g.setColor(Color.WHITE);
                        g.fillOval(4 + col*40, 4 + row*40, 35, 35);
                        g.setColor(Color.BLACK);
                        g.drawString("K", 16 + col*40, 26 + row*40);
                        break;
                    case CheckersData.BLACK_KING:
                        g.setColor(Color.BLACK);
                        g.fillOval(4 + col*40, 4 + row*40, 35, 35);
                        g.setColor(Color.WHITE);
                        g.drawString("K", 16 + col*40, 26 + row*40);
                        break;
                }
            }
        }

        if (gameInProgress) {
            g.setColor(Color.cyan);
            for (CheckersMove legalMove : legalMoves) {
                g.drawRect(2 + legalMove.fromCol * 40, 2 + legalMove.fromRow * 40, 39, 39);
                g.drawRect(3 + legalMove.fromCol * 40, 3 + legalMove.fromRow * 40, 37, 37);
            }
            if (selectedRow >= 0) {
                g.setColor(Color.white);
                g.drawRect(2 + selectedCol*40, 2 + selectedRow*40, 39, 39);
                g.drawRect(3 + selectedCol*40, 3 + selectedRow*40, 37, 37);
                g.setColor(Color.green);
                for (CheckersMove legalMove : legalMoves) {
                    if (legalMove.fromCol == selectedCol && legalMove.fromRow == selectedRow) {
                        g.drawRect(2 + legalMove.toCol * 40, 2 + legalMove.toRow * 40, 39, 39);
                        g.drawRect(3 + legalMove.toCol * 40, 3 + legalMove.toRow * 40, 37, 37);
                    }
                }
            }
        }

    }

    public void mousePressed(MouseEvent evt) {
        if (!gameInProgress)
            message.setText("Начни новую игру");
        else {
            int col = (evt.getX() - 2) / 40;
            int row = (evt.getY() - 2) / 40;
            if (col >= 0 && col < 8 && row >= 0 && row < 8)
                doClickSquare(row,col);
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