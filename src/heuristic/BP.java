package heuristic;

import core.Board;
import core.GameState;
import core.Piece;

/**
 * Blocking Pieces heuristic.
 * Counts pieces in path.
 */
public class BP extends Heuristic {
    
    @Override
    public int evaluate(GameState state) {
        Board board = state.getBoard();
        Piece primary = board.getPrimaryPiece();
        char[][] grid = board.getGrid(); // Use current grid
        
        if (primary == null) return Integer.MAX_VALUE;
        
        int blockingCount = 0;
        int exitR = board.getExitRow();
        int exitC = board.getExitCol();
        
        if (primary.isHorizontal()) {
            int r = primary.getRow();
            // Path to right exit
            if (exitC >= primary.getCol() + primary.getSize() || exitC == board.getCols()) {
                for (int c = primary.getCol() + primary.getSize(); c < board.getCols(); c++) {
                    if (c == exitC && r == exitR) break; // Reached in-grid exit
                    if (grid[r][c] != '.') blockingCount++;
                }
            } 
            // Path to left exit
            else if (exitC < primary.getCol() || exitC == -1) {
                for (int c = primary.getCol() - 1; c >= 0; c--) {
                    if (c == exitC && r == exitR) break; // Reached in-grid exit
                    if (grid[r][c] != '.') blockingCount++;
                }
            }
        } else { // Vertical
            int c = primary.getCol();
            // Path to bottom exit
            if (exitR >= primary.getRow() + primary.getSize() || exitR == board.getRows()) {
                for (int r = primary.getRow() + primary.getSize(); r < board.getRows(); r++) {
                    if (r == exitR && c == exitC) break;
                    if (grid[r][c] != '.') blockingCount++;
                }
            }
            // Path to top exit
            else if (exitR < primary.getRow() || exitR == -1) {
                for (int r = primary.getRow() - 1; r >= 0; r--) {
                    if (r == exitR && c == exitC) break;
                    if (grid[r][c] != '.') blockingCount++;
                }
            }
        }
        return blockingCount;
    }
    
    @Override
    public String getName() {
        return "Blocking Pieces";
    }
}