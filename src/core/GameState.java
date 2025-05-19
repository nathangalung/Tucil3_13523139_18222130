package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a game state.
 */
public class GameState {
    private final Board board;
    private final GameState parent;
    private final Move lastMove;
    private final int cost;
    
    public GameState(Board board) {
        this(board, null, null, 0);
    }
    
    public GameState(Board board, GameState parent, Move lastMove) {
        this(board, parent, lastMove, (parent != null ? parent.getCost() + 1 : 0));
    }

    private GameState(Board board, GameState parent, Move lastMove, int cost) {
        this.board = board;
        this.parent = parent;
        this.lastMove = lastMove;
        this.cost = cost;
    }
    
    public Board getBoard() { return board; }
    public GameState getParent() { return parent; }
    public Move getLastMove() { return lastMove; }
    public int getCost() { return cost; }
    
    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        char[][] grid = board.getGrid(); // Use a copy
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (Piece piece : board.getPieces()) {
            int r = piece.getRow();
            int c = piece.getCol();
            int size = piece.getSize();
            
            if (piece.isHorizontal()) {
                // Check LEFT (within grid)
                int maxStepsLeftInGrid = 0;
                for (int i = c - 1; i >= 0 && grid[r][i] == '.'; i--) maxStepsLeftInGrid++;
                if (maxStepsLeftInGrid > 0) moves.add(new Move(piece, Move.LEFT, maxStepsLeftInGrid));
                
                // Check LEFT (to exit at col = -1)
                if (c == 0 && board.getExitCol() == -1 && board.getExitRow() == r) {
                    moves.add(new Move(piece, Move.LEFT, 1));
                }

                // Check RIGHT (within grid)
                int maxStepsRightInGrid = 0;
                for (int i = c + size; i < cols && grid[r][i] == '.'; i++) maxStepsRightInGrid++;
                if (maxStepsRightInGrid > 0) moves.add(new Move(piece, Move.RIGHT, maxStepsRightInGrid));

                // Check RIGHT (to exit at col = cols)
                if ((c + size -1) == (cols - 1) && board.getExitCol() == cols && board.getExitRow() == r) {
                    moves.add(new Move(piece, Move.RIGHT, 1));
                }

            } else { // Vertical
                // Check UP (within grid)
                int maxStepsUpInGrid = 0;
                for (int i = r - 1; i >= 0 && grid[i][c] == '.'; i--) maxStepsUpInGrid++;
                if (maxStepsUpInGrid > 0) moves.add(new Move(piece, Move.UP, maxStepsUpInGrid));

                // Check UP (to exit at row = -1)
                if (r == 0 && board.getExitRow() == -1 && board.getExitCol() == c) {
                    moves.add(new Move(piece, Move.UP, 1));
                }
                
                // Check DOWN (within grid)
                int maxStepsDownInGrid = 0;
                for (int i = r + size; i < rows && grid[i][c] == '.'; i++) maxStepsDownInGrid++;
                if (maxStepsDownInGrid > 0) moves.add(new Move(piece, Move.DOWN, maxStepsDownInGrid));

                // Check DOWN (to exit at row = rows)
                if ((r + size - 1) == (rows - 1) && board.getExitRow() == rows && board.getExitCol() == c) {
                    moves.add(new Move(piece, Move.DOWN, 1));
                }
            }
        }
        return moves;
    }
    
    // Apply move, return new state.
    public GameState applyMove(Move move) {
        Board newBoard = board.copy();
        Piece movingPiece = null;
        for (Piece p : newBoard.getPieces()) { 
            if (p.getId() == move.getPiece().getId()) {
                movingPiece = p;
                break;
            }
        }
        
        if (movingPiece != null) {
            switch (move.getDirection()) {
                case Move.UP    -> movingPiece.setRow(movingPiece.getRow() - move.getSteps());
                case Move.RIGHT -> movingPiece.setCol(movingPiece.getCol() + move.getSteps());
                case Move.DOWN  -> movingPiece.setRow(movingPiece.getRow() + move.getSteps());
                case Move.LEFT  -> movingPiece.setCol(movingPiece.getCol() - move.getSteps());
            }
            newBoard.updateGrid();
        }
        return new GameState(newBoard, this, move);
    }
    
    // Reconstruct solution path.
    public List<GameState> getSolutionPath() {
        List<GameState> path = new ArrayList<>();
        GameState current = this;
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        return path;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return Objects.equals(board.toString(), gameState.board.toString()); 
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(board.toString());
    }
}