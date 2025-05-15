package algorithm;

import core.GameState;
import core.Move;
import heuristic.Heuristic;

import java.util.*;

/**
 * Base pathfinding algorithm
 */
public abstract class PathFinder {
    protected Heuristic heuristic;
    protected int nodesVisited;
    protected long startTime;
    protected long endTime;
    
    public PathFinder(Heuristic heuristic) {
        this.heuristic = heuristic;
        this.nodesVisited = 0;
    }
    
    /**
     * Find path to solution
     */
    public abstract GameState findPath(GameState initialState);
    
    /**
     * Get algorithm name
     */
    public abstract String getName();
    
    /**
     * Get nodes visited count
     */
    public int getNodesVisited() {
        return nodesVisited;
    }
    
    /**
     * Get execution time in ms
     */
    public long getExecutionTime() {
        return endTime - startTime;
    }
    
    /**
     * Start time tracking
     */
    protected void startTimer() {
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Stop time tracking
     */
    protected void stopTimer() {
        endTime = System.currentTimeMillis();
    }
}