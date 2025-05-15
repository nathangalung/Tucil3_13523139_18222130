package core;

/**
 * Represents a vehicle on the board
 */
public class Piece {
    private char id;
    private boolean isPrimary;
    private boolean isHorizontal;
    private int size;
    private int row;
    private int col;

    public Piece(char id, boolean isPrimary, boolean isHorizontal, int size, int row, int col) {
        this.id = id;
        this.isPrimary = isPrimary;
        this.isHorizontal = isHorizontal;
        this.size = size;
        this.row = row;
        this.col = col;
    }

    public char getId() {
        return id;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public int getSize() {
        return size;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Piece copy() {
        return new Piece(id, isPrimary, isHorizontal, size, row, col);
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}