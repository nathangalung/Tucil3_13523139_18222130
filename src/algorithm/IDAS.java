package algorithm;

import core.GameState;
import core.Move;
import heuristic.Heuristic;

import java.util.*;

/**
 * Iterative Deepening A* Search
 */
public class IDAS extends PathFinder {
    private GameState solution;
    
    public IDAS(Heuristic heuristic) {
        super(heuristic);
    }
    
    @Override
    public GameState findPath(GameState initialState) {
        startTimer();
        solution = null;
        
        int threshold = heuristic.evaluate(initialState);
        
        while (solution == null) {
            int nextThreshold = Integer.MAX_VALUE;
            int t = search(initialState, 0, threshold, nextThreshold);
            
            if (t == -1) {
                stopTimer();
                return solution;
            }
            
            if (t == Integer.MAX_VALUE) {
                stopTimer();
                return null; // No solution
            }
            
            threshold = t;
        }
        
        stopTimer();
        return solution;
    }
    
    /**
     * Recursive IDAS search
     */
    private int search(GameState state, int g, int threshold, int nextThreshold) {
        nodesVisited++;
        
        int f = g + heuristic.evaluate(state);
        
        if (f > threshold) {
            return f;
        }
        
        if (state.getBoard().isWin()) {
            solution = state;
            return -1;
        }
        
        int min = Integer.MAX_VALUE;
        
        for (Move move : state.getPossibleMoves()) {
            GameState nextState = state.applyMove(move);
            int t = search(nextState, g + 1, threshold, nextThreshold);
            
            if (t == -1) {
                return -1; // Solution found
            }
            
            if (t < min) {
                min = t;
            }
        }
        
        return min;
    }
    
    @Override
    public String getName() {
        return "Iterative Deepening A*";
    }
}