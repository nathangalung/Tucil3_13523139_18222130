package algorithm;

import core.GameState;
import core.Move;
import heuristic.Heuristic;

/**
 * Iterative Deepening A*.
 * Depth-first with threshold.
 */
public class IDAS extends PathFinder {
    private GameState solutionState;
    
    public IDAS(Heuristic heuristic) {
        super(heuristic);
    }
    
    @Override
    public GameState findPath(GameState initialState) {
        startTimer();
        solutionState = null;
        
        int threshold = heuristic.evaluate(initialState);
        
        while (solutionState == null) {
            SearchResult result = searchRecursive(initialState, 0, threshold);
            nodesVisited += result.nodesThisIteration;

            if (result.status == SearchStatus.FOUND) {
                stopTimer();
                return solutionState;
            }
            if (result.status == SearchStatus.NOT_FOUND_EXHAUSTED) {
                stopTimer();
                return null;
            }
            threshold = result.nextThreshold; 
        }
        stopTimer(); 
        return solutionState; 
    }
    
    private SearchResult searchRecursive(GameState state, int gCost, int currentThreshold) {
        int fCost = gCost + heuristic.evaluate(state);
        int nodesThisIteration = 1; 

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
        final int nextThreshold;
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