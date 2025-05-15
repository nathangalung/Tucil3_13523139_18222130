package heuristic;

import core.Board;
import core.GameState;
import core.Piece;

/**
 * Blocking Pieces heuristic
 */
public class BP extends Heuristic {
    
    @Override
    public int evaluate(GameState state) {
        Board board = state.getBoard();
        Piece primaryPiece = board.getPrimaryPiece();
        char[][] grid = board.getGrid();
        
        if (primaryPiece == null) {
            return Integer.MAX_VALUE;
        }
        
        int blockingCount = 0;
        
        // Count pieces blocking the path to exit
        if (primaryPiece.isHorizontal()) {
            int row = primaryPiece.getRow();
            int startCol = primaryPiece.getCol() + primaryPiece.getSize();
            
            for (int col = startCol; col < board.getCols(); col++) {
                if (grid[row][col] != '.' && grid[row][col] != 'K') {
                    blockingCount++;
                }
            }
        } else {
            int col = primaryPiece.getCol();
            int startRow = primaryPiece.getRow() + primaryPiece.getSize();
            
            for (int row = startRow; row < board.getRows(); row++) {
                if (grid[row][col] != '.' && grid[row][col] != 'K') {
                    blockingCount++;
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