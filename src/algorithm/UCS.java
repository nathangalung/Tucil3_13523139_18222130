package algorithm;

import core.GameState;
import core.Move;
import java.util.*;

/**
 * Standalone Uniform Cost Search without interface dependency.
 */
public class UCS {
    private int nodesVisited;
    private long executionTime;

    /**
     * Resets metrics.
     */
    public UCS() {
        this.nodesVisited = 0;
        this.executionTime = 0;
    }

    /**
     * Solve the puzzle starting from the given state, returning the goal state.
     * @param initialState initial GameState
     * @return goal GameState with solution path, or null if no solution
     */
    public GameState solve(GameState initialState) {
        long startTime = System.currentTimeMillis();
        nodesVisited = 0;

        // Frontier ordered by actual cost g(n)
        PriorityQueue<GameState> frontier = new PriorityQueue<>(Comparator.comparingInt(GameState::getCost));
        Set<String> visited = new HashSet<>();
        frontier.add(initialState);

        while (!frontier.isEmpty()) {
            GameState current = frontier.poll();
            nodesVisited++;

            // Check goal condition
            if (current.getBoard().isWin()) {
                executionTime = System.currentTimeMillis() - startTime;
                return current;
            }

            String key = current.getBoard().toString();
            if (visited.contains(key)) continue;
            visited.add(key);

            // Expand all possible moves
            for (Move move : current.getPossibleMoves()) {
                GameState next = current.applyMove(move);
                String nextKey = next.getBoard().toString();
                if (!visited.contains(nextKey)) {
                    frontier.add(next);
                }
            }
        }

        executionTime = System.currentTimeMillis() - startTime;
        return null; // no solution found
    }

    /**
     * @return number of nodes (states) visited during search
     */
    public int getNodesVisited() {
        return nodesVisited;
    }

    /**
     * @return time in milliseconds spent on search
     */
    public long getExecutionTime() {
        return executionTime;
    }
    public String getName() {
        return "Uniform Cost Search";
    }
}