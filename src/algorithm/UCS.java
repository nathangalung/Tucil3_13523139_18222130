package algorithm;

import core.GameState;
import core.Move;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Uniform Cost Search.
 * Finds shortest path.
 */
public class UCS { // Not extending PathFinder as it's standalone
    private int nodesVisited;
    private long executionTimeMillis;

    public UCS() {
        // Constructor for UCS
    }

    // Solve puzzle.
    public GameState solve(GameState initialState) {
        long startTime = System.currentTimeMillis();
        nodesVisited = 0;

        PriorityQueue<GameState> frontier = new PriorityQueue<>(Comparator.comparingInt(GameState::getCost));
        Set<String> visitedStates = new HashSet<>(); // Visited board configurations
        frontier.add(initialState);

        while (!frontier.isEmpty()) {
            GameState current = frontier.poll();
            nodesVisited++;

            if (current.getBoard().isWin()) {
                executionTimeMillis = System.currentTimeMillis() - startTime;
                return current; // Solution found
            }

            String currentBoardKey = current.getBoard().toString();
            if (visitedStates.contains(currentBoardKey)) continue;
            visitedStates.add(currentBoardKey);

            for (Move move : current.getPossibleMoves()) {
                GameState next = current.applyMove(move);
                if (!visitedStates.contains(next.getBoard().toString())) {
                    frontier.add(next);
                }
            }
        }
        executionTimeMillis = System.currentTimeMillis() - startTime;
        return null; // No solution
    }

    // Get nodes visited.
    public int getNodesVisited() {
        return nodesVisited;
    }

    // Get execution time (ms).
    public long getExecutionTime() {
        return executionTimeMillis;
    }

    // Get algorithm name.
    public String getName() {
        return "Uniform Cost Search";
    }
}