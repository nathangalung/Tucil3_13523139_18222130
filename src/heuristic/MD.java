package heuristic;

import core.Board;
import core.GameState;
import core.Piece;

/**
 * Manhattan Distance heuristic.
 * Estimates moves to exit.
 */
public class MD extends Heuristic {
    
    @Override
    public int evaluate(GameState state) {
        Board board = state.getBoard();
        Piece primary = board.getPrimaryPiece();
        
        if (primary == null) return Integer.MAX_VALUE; // Should not happen
        
        int exitR = board.getExitRow();
        int exitC = board.getExitCol();

        if (primary.isHorizontal()) {
            // Target column for right exit is 'cols', for left is -1.
            // If exit is *within* grid, target is exitCol.
            int targetCol = (exitC == board.getCols() || exitC == -1) ? exitC : exitC;
            if (targetCol == board.getCols()) { // Exit right
                 return Math.max(0, targetCol - (primary.getCol() + primary.getSize()));
            } else if (targetCol == -1) { // Exit left
                 return Math.max(0, primary.getCol() - targetCol);
            } else { // Exit within grid
                 return Math.abs(primary.getCol() - targetCol);
            }
        } else { // Vertical
            int targetRow = (exitR == board.getRows() || exitR == -1) ? exitR : exitR;
            if (targetRow == board.getRows()) { // Exit bottom
                return Math.max(0, targetRow - (primary.getRow() + primary.getSize()));
            } else if (targetRow == -1) { // Exit top
                return Math.max(0, primary.getRow() - targetRow);
            } else { // Exit within grid
                return Math.abs(primary.getRow() - targetRow);
            }
        }
    }
    
    @Override
    public String getName() {
        return "Manhattan Distance";
    }
}