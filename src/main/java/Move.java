class Move {
    int row1, col1;
    int row2, col2;
    Move(int r1, int c1, int r2, int c2) {
        row1 = r1;
        col1 = c1;
        row2 = r2;
        col2 = c2;
    }
    boolean isJump() {
        return (row1 - row2 == 2 || row1 - row2 == -2);
    }
}