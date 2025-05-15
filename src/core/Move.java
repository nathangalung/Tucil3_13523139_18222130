package core;

/**
 * Represents a piece movement
 */
public class Move {
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    
    private Piece piece;
    private int direction;
    private int steps;
    
    public Move(Piece piece, int direction, int steps) {
        this.piece = piece;
        this.direction = direction;
        this.steps = steps;
    }
    
    public Piece getPiece() {
        return piece;
    }
    
    public int getDirection() {
        return direction;
    }
    
    public int getSteps() {
        return steps;
    }
    
    /**
     * Convert direction to string
     */
    public String getDirectionString() {
        switch (direction) {
            case UP: return "atas";
            case RIGHT: return "kanan";
            case DOWN: return "bawah";
            case LEFT: return "kiri";
            default: return "unknown";
        }
    }
    
    @Override
    public String toString() {
        return piece.getId() + "-" + getDirectionString();
    }
}