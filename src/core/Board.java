package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the game board.
 */
public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid; 
    private final List<Piece> pieces;
    private int exitRow;
    private int exitCol;
    private final boolean[] isSpaceOnlyRow; 
    private final boolean[] isSpaceOnlyCol; 
    
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.pieces = new ArrayList<>();
        this.exitRow = -1; 
        this.exitCol = -1; 
        this.isSpaceOnlyRow = new boolean[rows];
        this.isSpaceOnlyCol = new boolean[cols];
        
        for (char[] rowGrid : this.grid) {
            Arrays.fill(rowGrid, '.'); 
        }
    }

    // Set exit location.
    public void setExit(int r, int c) {
        this.exitRow = r;
        this.exitCol = c;
    }
    
    // Initialize from configuration.
    public void initialize(char[][] originalConfiguration) {
        Map<Character, List<int[]>> piecePositions = new HashMap<>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = originalConfiguration[i][j];
                if (c == ' ' || c == '\0') {
                    this.grid[i][j] = '.';
                } else if (c != '.') {
                    this.grid[i][j] = c; 
                    piecePositions.computeIfAbsent(c, k -> new ArrayList<>()).add(new int[]{i, j});
                } else { 
                    this.grid[i][j] = '.';
                }
            }
        }
        
        for (Map.Entry<Character, List<int[]>> entry : piecePositions.entrySet()) {
            char id = entry.getKey();
            List<int[]> positions = entry.getValue();
            if (positions.isEmpty()) continue;

            positions.sort((a, b) -> (a[0] != b[0]) ? a[0] - b[0] : a[1] - b[1]);
            
            int size = positions.size();
            int startRow = positions.get(0)[0];
            int startCol = positions.get(0)[1];
            boolean isHorizontal = (size > 1) && (positions.get(0)[0] == positions.get(1)[0]);
            
            pieces.add(new Piece(id, (id == 'P'), isHorizontal, size, startRow, startCol));
        }

        for (int i = 0; i < rows; i++) {
            isSpaceOnlyRow[i] = true; 
            for (int j = 0; j < cols; j++) {
                if (originalConfiguration[i][j] != ' ') {
                    isSpaceOnlyRow[i] = false;
                    break;
                }
            }
        }
        for (int j = 0; j < cols; j++) {
            isSpaceOnlyCol[j] = true;
            for (int i = 0; i < rows; i++) {
                if (originalConfiguration[i][j] != ' ') {
                    isSpaceOnlyCol[j] = false;
                    break;
                }
            }
        }
        
        if (this.exitRow == -1 && this.exitCol == -1) {
            Piece primary = getPrimaryPiece();
            if (primary != null) {
                if (primary.isHorizontal()) {
                    exitRow = primary.getRow();
                    exitCol = (primary.getCol() + primary.getSize() / 2.0 > cols / 2.0) ? cols : -1;
                } else {
                    exitCol = primary.getCol();
                    exitRow = (primary.getRow() + primary.getSize() / 2.0 > rows / 2.0) ? rows : -1;
                }
            }
        }
        updateGrid(); 
    }

    public boolean isRowSpaceOnly(int r) { return (r >= 0 && r < rows) && isSpaceOnlyRow[r]; }

    public boolean isColSpaceOnly(int c) { return (c >= 0 && c < cols) && isSpaceOnlyCol[c]; }
    
    // Get the primary piece.
    public Piece getPrimaryPiece() {
        for (Piece piece : pieces) {
            if (piece.isPrimary()) return piece;
        }
        return null;
    }
    
    // Get all pieces.
    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }
    
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getExitRow() { return exitRow; }
    public int getExitCol() { return exitCol; }
    
    // Update logical grid.
    public void updateGrid() {
        for (char[] rowGrid : this.grid) {
            Arrays.fill(rowGrid, '.');
        }
        for (Piece piece : pieces) {
            int r = piece.getRow();
            int c = piece.getCol();
            for (int s = 0; s < piece.getSize(); s++) {
                if (piece.isHorizontal()) {
                    if (c + s >= 0 && c + s < cols && r >=0 && r < rows) grid[r][c + s] = piece.getId();
                } else {
                    if (r + s >= 0 && r + s < rows && c >=0 && c < cols) grid[r + s][c] = piece.getId();
                }
            }
        }
    }
    
    // Get current grid state.
    public char[][] getGrid() {
        char[][] currentGrid = new char[rows][cols];
        for(int i=0; i<rows; i++) {
            System.arraycopy(this.grid[i], 0, currentGrid[i], 0, cols);
        }
        return currentGrid;
    }
    
    // Create a deep copy.
    public Board copy() {
        Board newBoard = new Board(rows, cols);
        newBoard.exitRow = this.exitRow;
        newBoard.exitCol = this.exitCol;
        
        for (Piece piece : this.pieces) {
            newBoard.pieces.add(piece.copy());
        }
        
        for(int i=0; i<rows; i++) {
            System.arraycopy(this.grid[i], 0, newBoard.grid[i], 0, cols);
        }
        System.arraycopy(this.isSpaceOnlyRow, 0, newBoard.isSpaceOnlyRow, 0, rows);
        System.arraycopy(this.isSpaceOnlyCol, 0, newBoard.isSpaceOnlyCol, 0, cols);
        
        return newBoard;
    }
    
    // Check win condition.
    public boolean isWin() {
        Piece primary = getPrimaryPiece();
        if (primary == null) return false;
        
        int pRow = primary.getRow();
        int pCol = primary.getCol();
        int pSize = primary.getSize();
        
        if (primary.isHorizontal()) {
            if (exitRow >= 0 && exitRow < rows && pRow != exitRow) return false; 
            if (exitCol == cols) return pRow == exitRow && (pCol + pSize >= cols); 
            if (exitCol == -1) return pRow == exitRow && (pCol <= exitCol);    
            if (exitCol >= 0 && exitCol < cols) 
                return pRow == exitRow && (pCol <= exitCol && (pCol + pSize - 1) >= exitCol);
        } else { 
            if (exitCol >= 0 && exitCol < cols && pCol != exitCol) return false;
            if (exitRow == rows) return pCol == exitCol && (pRow + pSize >= rows);
            if (exitRow == -1) return pCol == exitCol && (pRow <= exitRow);      
            if (exitRow >= 0 && exitRow < rows) 
                return pCol == exitCol && (pRow <= exitRow && (pRow + pSize - 1) >= exitRow);
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(grid[i][j]);
            }
            if (i < rows - 1) sb.append("\n");
        }
        return sb.toString();
    }
}