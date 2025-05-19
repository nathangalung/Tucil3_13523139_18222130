package core;

/**
 * Represents a vehicle piece.
 */
public class Piece {
    private final char id;
    private final boolean isPrimary;
    private final boolean isHorizontal;
    private final int size;
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

    // Get piece identifier.
    public char getId() {
        return id;
    }

    // Is primary piece?
    public boolean isPrimary() {
        return isPrimary;
    }

    // Is piece horizontal?
    public boolean isHorizontal() {
        return isHorizontal;
    }

    // Get piece size.
    public int getSize() {
        return size;
    }

    // Get piece row.
    public int getRow() {
        return row;
    }

    // Get piece column.
    public int getCol() {
        return col;
    }

    // Set piece row.
    public void setRow(int row) {
        this.row = row;
    }

    // Set piece column.
    public void setCol(int col) {
        this.col = col;
    }

    // Create a copy.
    public Piece copy() {
        return new Piece(id, isPrimary, isHorizontal, size, row, col);
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}