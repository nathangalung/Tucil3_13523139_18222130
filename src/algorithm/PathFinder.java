package algorithm;

import core.GameState;
import heuristic.Heuristic;

/**
 * Abstract base for pathfinders.
 */
public abstract class PathFinder {
    protected final Heuristic heuristic;
    protected int nodesVisited;
    protected long executionTimeMillis;
    
    public PathFinder(Heuristic heuristic) {
        this.heuristic = heuristic;
    }
    
    // Find path to solution.
    public abstract GameState findPath(GameState initialState);
    
    // Get algorithm name.
    public abstract String getName();
    
    // Get nodes visited.
    public int getNodesVisited() {
        return nodesVisited;
    }
    
    // Get execution time (ms).
    public long getExecutionTime() {
        return executionTimeMillis;
    }
    
    // Start timing search.
    protected void startTimer() {
        this.nodesVisited = 0;
        this.executionTimeMillis = System.currentTimeMillis();
    }
    
    // Stop timing search.
    protected void stopTimer() {
        this.executionTimeMillis = System.currentTimeMillis() - this.executionTimeMillis;
    }
}