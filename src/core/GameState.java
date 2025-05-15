package core;

import java.util.*;

/**
 * Represents current game configuration
 */
public class GameState {
    private Board board;
    private GameState parent;
    private Move lastMove;
    private int cost;
    
    public GameState(Board board) {
        this.board = board;
        this.parent = null;
        this.lastMove = null;
        this.cost = 0;
    }
    
    public GameState(Board board, GameState parent, Move lastMove) {
        this.board = board;
        this.parent = parent;
        this.lastMove = lastMove;
        this.cost = parent.getCost() + 1;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public GameState getParent() {
        return parent;
    }
    
    public Move getLastMove() {
        return lastMove;
    }
    
    public int getCost() {
        return cost;
    }
    
    /**
     * Get all possible moves
     */
    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        char[][] grid = board.getGrid();
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (Piece piece : board.getPieces()) {
            int r = piece.getRow();
            int c = piece.getCol();
            int size = piece.getSize();
            
            if (piece.isHorizontal()) {
                // Check left moves
                int maxLeft = 0;
                for (int i = c - 1; i >= 0; i--) {
                    if (grid[r][i] == '.') {
                        maxLeft++;
                    } else {
                        break;
                    }
                }
                if (maxLeft > 0) {
                    moves.add(new Move(piece, Move.LEFT, maxLeft));
                }
                
                // Check right moves
                int maxRight = 0;
                for (int i = c + size; i < cols; i++) {
                    if (grid[r][i] == '.') {
                        maxRight++;
                    } else {
                        break;
                    }
                }
                if (maxRight > 0) {
                    moves.add(new Move(piece, Move.RIGHT, maxRight));
                }
            } else {
                // Check up moves
                int maxUp = 0;
                for (int i = r - 1; i >= 0; i--) {
                    if (grid[i][c] == '.') {
                        maxUp++;
                    } else {
                        break;
                    }
                }
                if (maxUp > 0) {
                    moves.add(new Move(piece, Move.UP, maxUp));
                }
                
                // Check down moves
                int maxDown = 0;
                for (int i = r + size; i < rows; i++) {
                    if (grid[i][c] == '.') {
                        maxDown++;
                    } else {
                        break;
                    }
                }
                if (maxDown > 0) {
                    moves.add(new Move(piece, Move.DOWN, maxDown));
                }
            }
        }
        
        return moves;
    }
    
    /**
     * Apply move to create new state
     */
    public GameState applyMove(Move move) {
        Board newBoard = board.copy();
        List<Piece> pieces = newBoard.getPieces();
        Piece movingPiece = null;
        
        // Find the piece to move
        for (Piece p : pieces) {
            if (p.getId() == move.getPiece().getId()) {
                movingPiece = p;
                break;
            }
        }
        
        // Apply move
        if (movingPiece != null) {
            switch (move.getDirection()) {
                case Move.UP:
                    movingPiece.setRow(movingPiece.getRow() - move.getSteps());
                    break;
                case Move.RIGHT:
                    movingPiece.setCol(movingPiece.getCol() + move.getSteps());
                    break;
                case Move.DOWN:
                    movingPiece.setRow(movingPiece.getRow() + move.getSteps());
                    break;
                case Move.LEFT:
                    movingPiece.setCol(movingPiece.getCol() - move.getSteps());
                    break;
            }
        }
        
        newBoard.updateGrid();
        return new GameState(newBoard, this, move);
    }
    
    /**
     * Get solution path
     */
    public List<GameState> getSolutionPath() {
        List<GameState> path = new ArrayList<>();
        GameState current = this;
        
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        
        return path;
    }
    
    /**
     * Equals based on board configuration
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return board.toString().equals(gameState.board.toString());
    }
    
    /**
     * Hash based on board configuration
     */
    @Override
    public int hashCode() {
        return board.toString().hashCode();
    }
}