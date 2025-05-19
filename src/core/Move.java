package core;

/**
 * Represents a piece movement.
 */
public class Move {
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    
    private final Piece piece;
    private final int direction;
    private final int steps;
    
    public Move(Piece piece, int direction, int steps) {
        this.piece = piece;
        this.direction = direction;
        this.steps = steps;
    }
    
    // Get moved piece.
    public Piece getPiece() {
        return piece;
    }
    
    // Get move direction.
    public int getDirection() {
        return direction;
    }
    
    // Get move steps.
    public int getSteps() {
        return steps;
    }
    
    // Get direction as string.
    public String getDirectionString() {
        return switch (direction) {
            case UP -> "atas";
            case RIGHT -> "kanan";
            case DOWN -> "bawah";
            case LEFT -> "kiri";
            default -> "unknown";
        };
    }
    
    @Override
    public String toString() {
        return piece.getId() + "-" + getDirectionString();
    }
}