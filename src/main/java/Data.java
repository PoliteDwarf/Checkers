import java.awt.*;
import java.util.ArrayList;

class Data {

    enum Check {
         EMPTY,
         WHITE,
         WHITEQ,
         BLACK,
         BLACKQ
     }

    private Check[][] board;

    Data() {
        board = new Check[8][8];
    }

    void setUp() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 != col % 2 ) {
                    if (row < 3)
                        board[row][col] = Check.BLACK;
                    else if (row > 4)
                        board[row][col] = Check.WHITE;
                    else
                        board[row][col] = Check.EMPTY;
                }
                else {
                    board[row][col] = Check.EMPTY;
                }
            }
        }
    }

    Check pieceAt(int row, int col) {
        return board[row][col];
    }

    void makeMove(Move move) {
        makeMove(move.row1, move.col1, move.row2, move.col2);
    }

    private void makeMove(int row1, int col1, int row2, int col2) {
        board[row2][col2] = board[row1][col1];
        board[row1][col1] = Check.EMPTY;
        if (row1 - row2 == 2 || row1 - row2 == -2) {
            int jumpRow = (row1 + row2) / 2;
            int jumpCol = (col1 + col2) / 2;
            board[jumpRow][jumpCol] = Check.EMPTY;
        }
        if (row2 == 0 && board[row2][col2] == Check.WHITE)
            board[row2][col2] = Check.WHITEQ;
        if (row2 == 7 && board[row2][col2] == Check.BLACK)
            board[row2][col2] = Check.BLACKQ;
    }

    Move[] getMoves(Check player) {

        if (player != Check.WHITE && player != Check.BLACK)
            return null;

        Check playerQ;
        if (player == Check.WHITE)
            playerQ = Check.WHITEQ;
        else
            playerQ = Check.BLACKQ;

        ArrayList<Move> moves = new ArrayList<Move>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player || board[row][col] == playerQ) {
                    if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                        moves.add(new Move(row, col, row+2, col+2));
                    if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                        moves.add(new Move(row, col, row-2, col+2));
                    if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                        moves.add(new Move(row, col, row+2, col-2));
                    if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                        moves.add(new Move(row, col, row-2, col-2));
                }
            }
        }

        if (moves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerQ) {
                        if (canMove(player,row,col,row+1,col+1))
                            moves.add(new Move(row,col,row+1,col+1));
                        if (canMove(player,row,col,row-1,col+1))
                            moves.add(new Move(row,col,row-1,col+1));
                        if (canMove(player,row,col,row+1,col-1))
                            moves.add(new Move(row,col,row+1,col-1));
                        if (canMove(player,row,col,row-1,col-1))
                            moves.add(new Move(row,col,row-1,col-1));
                    }
                }
            }
        }

        if (moves.size() == 0)
            return null;
        else {
            Move[] moveArray = new Move[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }

    }

    Move[] getJumps(Check player, int row, int col) {
        if (player != Check.WHITE && player != Check.BLACK)
            return null;
        Check playerKing;
        if (player == Check.WHITE)
            playerKing = Check.WHITEQ;
        else
            playerKing = Check.BLACKQ;
        ArrayList<Move> moves = new ArrayList<Move>();
        if (board[row][col] == player || board[row][col] == playerKing) {
            if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                moves.add(new Move(row, col, row+2, col+2));
            if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                moves.add(new Move(row, col, row-2, col+2));
            if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                moves.add(new Move(row, col, row+2, col-2));
            if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                moves.add(new Move(row, col, row-2, col-2));
        }
        if (moves.size() == 0)
            return null;
        else {
            Move[] moveArray = new Move[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }
    }

    private boolean canJump(Check player, int r1, int c1, int r2, int c2, int r3, int c3) {

        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false;

        if (board[r3][c3] != Check.EMPTY)
            return false;

        if (player == Check.WHITE) {
            if (board[r1][c1] == Check.WHITE && r3 > r1)
                return false;
            return board[r2][c2] == Check.BLACK || board[r2][c2] == Check.BLACKQ;
        }
        else {
            if (board[r1][c1] == Check.BLACK && r3 < r1)
                return false;
            return board[r2][c2] == Check.WHITE || board[r2][c2] == Check.WHITEQ;
        }

    }

    private boolean canMove(Check player, int r1, int c1, int r2, int c2) {

        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false;

        if (board[r2][c2] != Check.EMPTY)
            return false;

        if (player == Check.WHITE) {
            return board[r1][c1] != Check.WHITE || r2 <= r1;
        }
        else {
            return board[r1][c1] != Check.BLACK || r2 >= r1;
        }

    }


}