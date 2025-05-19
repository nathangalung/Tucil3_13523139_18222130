package algorithm;

import core.GameState;
import core.Move;
import heuristic.Heuristic;
// No standard Java Set/List needed for core recursion

/**
 * Iterative Deepening A*.
 * Depth-first with threshold.
 */
public class IDAS extends PathFinder {
    private GameState solutionState; // Stores found solution
    
    public IDAS(Heuristic heuristic) {
        super(heuristic);
    }
    
    @Override
    public GameState findPath(GameState initialState) {
        startTimer();
        solutionState = null; // Reset
        
        int threshold = heuristic.evaluate(initialState);
        
        while (solutionState == null) {
            SearchResult result = searchRecursive(initialState, 0, threshold);
            nodesVisited += result.nodesThisIteration;

            if (result.status == SearchStatus.FOUND) {
                stopTimer();
                return solutionState; // Solution found
            }
            if (result.status == SearchStatus.NOT_FOUND_EXHAUSTED) {
                stopTimer();
                return null; // No solution possible
            }
            threshold = result.nextThreshold; // Update for next iteration
        }
        stopTimer(); // Should be unreachable if logic is correct
        return solutionState; 
    }
    
    // Recursive search helper.
    private SearchResult searchRecursive(GameState state, int gCost, int currentThreshold) {
        int fCost = gCost + heuristic.evaluate(state);
        int nodesThisIteration = 1; // Count current node

        if (fCost > currentThreshold) {
            return new SearchResult(SearchStatus.NOT_FOUND_WITHIN_THRESHOLD, fCost, nodesThisIteration);
        }
        
        if (state.getBoard().isWin()) {
            solutionState = state;
            return new SearchResult(SearchStatus.FOUND, fCost, nodesThisIteration);
        }
        
        int minNextThreshold = Integer.MAX_VALUE;
        
        for (Move move : state.getPossibleMoves()) {
            GameState nextState = state.applyMove(move);
            // Avoid simple loops by not going back to parent immediately
            if (state.getParent() != null && nextState.getBoard().toString().equals(state.getParent().getBoard().toString())) {
                continue;
            }

            SearchResult result = searchRecursive(nextState, gCost + 1, currentThreshold);
            nodesThisIteration += result.nodesThisIteration;

            if (result.status == SearchStatus.FOUND) {
                return new SearchResult(SearchStatus.FOUND, fCost, nodesThisIteration);
            }
            if (result.nextThreshold < minNextThreshold) {
                minNextThreshold = result.nextThreshold;
            }
        }
        return new SearchResult(SearchStatus.NOT_FOUND_WITHIN_THRESHOLD, minNextThreshold, nodesThisIteration);
    }

    private enum SearchStatus { FOUND, NOT_FOUND_WITHIN_THRESHOLD, NOT_FOUND_EXHAUSTED }

    private static class SearchResult {
        final SearchStatus status;
        final int nextThreshold; // Min f-cost exceeding current threshold
        final int nodesThisIteration;

        SearchResult(SearchStatus status, int nextThreshold, int nodesThisIteration) {
            this.status = status;
            this.nextThreshold = nextThreshold;
            this.nodesThisIteration = nodesThisIteration;
        }
    }
    
    @Override
    public String getName() {
        return "Iterative Deepening A*";
    }
}