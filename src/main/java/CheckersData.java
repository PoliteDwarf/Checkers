import java.util.ArrayList;

class CheckersData {

     static final int
            EMPTY = 0,
            WHITE = 1,
            WHITE_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    private int[][] board;

    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    void setUpGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 != col % 2 ) {
                    if (row < 3)
                        board[row][col] = BLACK;
                    else if (row > 4)
                        board[row][col] = WHITE;
                    else
                        board[row][col] = EMPTY;
                }
                else {
                    board[row][col] = EMPTY;
                }
            }
        }
    }

    int pieceAt(int row, int col) {
        return board[row][col];
    }

    void makeMove(CheckersMove move) {
        makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
    }

    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            int jumpRow = (fromRow + toRow) / 2;
            int jumpCol = (fromCol + toCol) / 2;
            board[jumpRow][jumpCol] = EMPTY;
        }
        if (toRow == 0 && board[toRow][toCol] == WHITE)
            board[toRow][toCol] = WHITE_KING;
        if (toRow == 7 && board[toRow][toCol] == BLACK)
            board[toRow][toCol] = BLACK_KING;
    }

    CheckersMove[] getLegalMoves(int player) {

        if (player != WHITE && player != BLACK)
            return null;

        int playerKing;
        if (player == WHITE)
            playerKing = WHITE_KING;
        else
            playerKing = BLACK_KING;

        ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player || board[row][col] == playerKing) {
                    if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                        moves.add(new CheckersMove(row, col, row+2, col+2));
                    if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                        moves.add(new CheckersMove(row, col, row-2, col+2));
                    if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                        moves.add(new CheckersMove(row, col, row+2, col-2));
                    if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                        moves.add(new CheckersMove(row, col, row-2, col-2));
                }
            }
        }

        if (moves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (canMove(player,row,col,row+1,col+1))
                            moves.add(new CheckersMove(row,col,row+1,col+1));
                        if (canMove(player,row,col,row-1,col+1))
                            moves.add(new CheckersMove(row,col,row-1,col+1));
                        if (canMove(player,row,col,row+1,col-1))
                            moves.add(new CheckersMove(row,col,row+1,col-1));
                        if (canMove(player,row,col,row-1,col-1))
                            moves.add(new CheckersMove(row,col,row-1,col-1));
                    }
                }
            }
        }

        if (moves.size() == 0)
            return null;
        else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }

    }

    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        if (player != WHITE && player != BLACK)
            return null;
        int playerKing;
        if (player == WHITE)
            playerKing = WHITE_KING;
        else
            playerKing = BLACK_KING;
        ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();
        if (board[row][col] == player || board[row][col] == playerKing) {
            if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                moves.add(new CheckersMove(row, col, row+2, col+2));
            if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                moves.add(new CheckersMove(row, col, row-2, col+2));
            if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                moves.add(new CheckersMove(row, col, row+2, col-2));
            if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                moves.add(new CheckersMove(row, col, row-2, col-2));
        }
        if (moves.size() == 0)
            return null;
        else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }
    }

    private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {

        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false;

        if (board[r3][c3] != EMPTY)
            return false;

        if (player == WHITE) {
            if (board[r1][c1] == WHITE && r3 > r1)
                return false;
            return board[r2][c2] == BLACK || board[r2][c2] == BLACK_KING;
        }
        else {
            if (board[r1][c1] == BLACK && r3 < r1)
                return false;
            return board[r2][c2] == WHITE || board[r2][c2] == WHITE_KING;
        }

    }

    private boolean canMove(int player, int r1, int c1, int r2, int c2) {

        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false;

        if (board[r2][c2] != EMPTY)
            return false;

        if (player == WHITE) {
            return board[r1][c1] != WHITE || r2 <= r1;
        }
        else {
            return board[r1][c1] != BLACK || r2 >= r1;
        }

    }


}