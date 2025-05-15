package heuristic;

import core.GameState;

/**
 * Distance + Blocking heuristic
 */
public class DB extends Heuristic {
    private MD mdHeuristic;
    private BP bpHeuristic;
    
    public DB() {
        this.mdHeuristic = new MD();
        this.bpHeuristic = new BP();
    }
    
    @Override
    public int evaluate(GameState state) {
        int distance = mdHeuristic.evaluate(state);
        int blocking = bpHeuristic.evaluate(state);
        
        // Combine the two heuristics
        return distance + 2 * blocking;
    }
    
    @Override
    public String getName() {
        return "Distance + Blocking";
    }
}