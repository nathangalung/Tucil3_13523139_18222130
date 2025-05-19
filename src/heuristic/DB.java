package heuristic;

import core.GameState;

/**
 * Distance + Blocking heuristic.
 * Combines MD and BP.
 */
public class DB extends Heuristic {
    private final MD mdHeuristic;
    private final BP bpHeuristic;
    
    public DB() {
        this.mdHeuristic = new MD();
        this.bpHeuristic = new BP();
    }
    
    @Override
    public int evaluate(GameState state) {
        // Weighted sum
        return mdHeuristic.evaluate(state) + bpHeuristic.evaluate(state);
    }
    
    @Override
    public String getName() {
        return "Distance + Blocking";
    }
}