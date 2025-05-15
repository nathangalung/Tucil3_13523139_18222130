package core;

import java.util.*;

/**
 * Rush Hour game board
 */
public class Board {
    private int rows;
    private int cols;
    private char[][] grid;
    private List<Piece> pieces;
    private int exitRow;
    private int exitCol;
    
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.pieces = new ArrayList<>();
        this.exitRow = -1;
        this.exitCol = -1;
        
        // Initialize empty grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
    }
    
    /**
     * Setup board from configuration
     */
    public void initialize(char[][] configuration) {
        Map<Character, List<int[]>> piecePositions = new HashMap<>();
        
        // Scan the board to find piece positions and exit
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = configuration[i][j];
                if (c == '.') {
                    grid[i][j] = '.';
                } else if (c == 'K') {
                    // Mark the exit position but don't include it in the grid
                    exitRow = i;
                    exitCol = j;
                    grid[i][j] = '.';
                } else {
                    grid[i][j] = c;
                    if (!piecePositions.containsKey(c)) {
                        piecePositions.put(c, new ArrayList<>());
                    }
                    piecePositions.get(c).add(new int[]{i, j});
                }
            }
        }
        
        // Create pieces from positions
        for (Map.Entry<Character, List<int[]>> entry : piecePositions.entrySet()) {
            char id = entry.getKey();
            List<int[]> positions = entry.getValue();
            boolean isPrimary = (id == 'P');
            
            // Sort positions by row and column
            positions.sort((a, b) -> {
                if (a[0] != b[0]) return a[0] - b[0];
                return a[1] - b[1];
            });
            
            int size = positions.size();
            int startRow = positions.get(0)[0];
            int startCol = positions.get(0)[1];
            boolean isHorizontal = true;
            
            // Determine orientation
            if (size > 1) {
                isHorizontal = positions.get(0)[0] == positions.get(1)[0];
            }
            
            Piece piece = new Piece(id, isPrimary, isHorizontal, size, startRow, startCol);
            pieces.add(piece);
        }
        
        // If no exit is found, set a default exit position
        if (exitRow == -1 || exitCol == -1) {
            // Determine primary piece orientation and set exit accordingly
            Piece primary = getPrimaryPiece();
            if (primary != null) {
                if (primary.isHorizontal()) {
                    // Exit on right side
                    exitRow = primary.getRow();
                    exitCol = cols;
                } else {
                    // Exit on bottom side
                    exitRow = rows;
                    exitCol = primary.getCol();
                }
            }
        }
    }
    
    public Piece getPrimaryPiece() {
        for (Piece piece : pieces) {
            if (piece.isPrimary()) {
                return piece;
            }
        }
        return null;
    }
    
    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public int getExitRow() {
        return exitRow;
    }
    
    public int getExitCol() {
        return exitCol;
    }
    
    /**
     * Update grid with pieces
     */
    public void updateGrid() {
        // Clear grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
        
        // Place pieces
        for (Piece piece : pieces) {
            int r = piece.getRow();
            int c = piece.getCol();
            boolean isHorizontal = piece.isHorizontal();
            
            for (int s = 0; s < piece.getSize(); s++) {
                if (isHorizontal) {
                    if (c + s < cols) {
                        grid[r][c + s] = piece.getId();
                    }
                } else {
                    if (r + s < rows) {
                        grid[r + s][c] = piece.getId();
                    }
                }
            }
        }
    }
    
    public char[][] getGrid() {
        return grid;
    }
    
    /**
     * Copy current board state
     */
    public Board copy() {
        Board copy = new Board(rows, cols);
        copy.exitRow = this.exitRow;
        copy.exitCol = this.exitCol;
        
        for (Piece piece : pieces) {
            copy.pieces.add(piece.copy());
        }
        
        copy.updateGrid();
        return copy;
    }
    
    /**
     * Check if game is won
     */
    public boolean isWin() {
        Piece primary = getPrimaryPiece();
        if (primary == null) return false;
        
        int primaryRow = primary.getRow();
        int primaryCol = primary.getCol();
        int primarySize = primary.getSize();
        
        // For horizontal primary pieces
        if (primary.isHorizontal()) {
            // If exit is on the right edge
            if (exitCol >= cols) {
                return primaryCol + primarySize > cols - 1;
            }
            // If exit is elsewhere
            return (primaryRow == exitRow && primaryCol + primarySize - 1 >= exitCol);
        } 
        // For vertical primary pieces
        else {
            // If exit is on the bottom edge
            if (exitRow >= rows) {
                return primaryRow + primarySize > rows - 1;
            }
            // If exit is elsewhere
            return (primaryCol == exitCol && primaryRow + primarySize - 1 >= exitRow);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(grid[i][j]);
            }
            if (i < rows - 1) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
}