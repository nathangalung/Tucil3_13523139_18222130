package algorithm;

import core.GameState;
import core.Move;
import heuristic.Heuristic;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A* Search algorithm.
 * Uses g(n) + h(n).
 */
public class AS extends PathFinder {
    
    public AS(Heuristic heuristic) {
        super(heuristic);
    }
    
    @Override
    public GameState findPath(GameState initialState) {
        startTimer();
        
        PriorityQueue<GameState> openSet = new PriorityQueue<>(
            Comparator.comparingInt(s -> s.getCost() + heuristic.evaluate(s))
        );
        Set<String> closedSet = new HashSet<>(); // Visited board configurations
        
        openSet.add(initialState);
        
        while (!openSet.isEmpty()) {
            GameState current = openSet.poll();
            nodesVisited++;
            
            if (current.getBoard().isWin()) {
                stopTimer();
                return current; // Solution found
            }
            
            String boardKey = current.getBoard().toString();
            if (closedSet.contains(boardKey)) continue;
            closedSet.add(boardKey);
            
            for (Move move : current.getPossibleMoves()) {
                GameState nextState = current.applyMove(move);
                if (!closedSet.contains(nextState.getBoard().toString())) {
                    openSet.add(nextState);
                }
            }
        }
        stopTimer();
        return null; // No solution
    }
    
    @Override
    public String getName() {
        return "A* Search";
    }
}