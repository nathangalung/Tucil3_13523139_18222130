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
        
        if (primary == null) return Integer.MAX_VALUE; 
        
        int exitR = board.getExitRow();
        int exitC = board.getExitCol();

        if (primary.isHorizontal()) {
            int targetCol = (exitC == board.getCols() || exitC == -1) ? exitC : exitC;
            if (targetCol == board.getCols()) { 
                 return Math.max(0, targetCol - (primary.getCol() + primary.getSize()));
            } else if (targetCol == -1) { 
                 return Math.max(0, primary.getCol() - targetCol);
            } else { 
                 return Math.abs(primary.getCol() - targetCol);
            }
        } else {
            int targetRow = (exitR == board.getRows() || exitR == -1) ? exitR : exitR;
            if (targetRow == board.getRows()) { 
                return Math.max(0, targetRow - (primary.getRow() + primary.getSize()));
            } else if (targetRow == -1) {
                return Math.max(0, primary.getRow() - targetRow);
            } else { 
                return Math.abs(primary.getRow() - targetRow);
            }
        }
    }
    
    @Override
    public String getName() {
        return "Manhattan Distance";
    }
}