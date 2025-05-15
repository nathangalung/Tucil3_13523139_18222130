package heuristic;

import core.Board;
import core.GameState;
import core.Piece;

/**
 * Manhattan Distance heuristic
 */
public class MD extends Heuristic {
    
    @Override
    public int evaluate(GameState state) {
        Board board = state.getBoard();
        Piece primaryPiece = board.getPrimaryPiece();
        
        if (primaryPiece == null) {
            return Integer.MAX_VALUE;
        }
        
        // Primary piece is horizontal
        if (primaryPiece.isHorizontal()) {
            int distance = board.getCols() - (primaryPiece.getCol() + primaryPiece.getSize());
            return distance;
        }
        // Primary piece is vertical
        else {
            int distance = board.getRows() - (primaryPiece.getRow() + primaryPiece.getSize());
            return distance;
        }
    }
    
    @Override
    public String getName() {
        return "Manhattan Distance";
    }
}