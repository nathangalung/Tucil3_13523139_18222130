package heuristic;

import core.Board;
import core.GameState;

/**
 * Base heuristic interface
 */
public abstract class Heuristic {
    /**
     * Calculate heuristic value
     */
    public abstract int evaluate(GameState state);
    
    /**
     * Get heuristic name
     */
    public abstract String getName();
}