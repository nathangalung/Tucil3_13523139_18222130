package heuristic;

import core.GameState;

/**
 * Abstract base for heuristics.
 */
public abstract class Heuristic {
    public abstract int evaluate(GameState state);
    
    public abstract String getName();
}