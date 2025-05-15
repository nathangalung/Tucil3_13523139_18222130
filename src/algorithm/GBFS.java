package algorithm;

import core.GameState;
import core.Move;
import heuristic.Heuristic;
import java.util.*;

/**
 * Greedy Best First Search
 */
public class GBFS extends PathFinder {
    
    public GBFS(Heuristic heuristic) {
        super(heuristic);
    }
    
    @Override
    public GameState findPath(GameState initialState) {
        startTimer();
        
        // Priority queue based on heuristic value
        PriorityQueue<GameState> openSet = new PriorityQueue<>(
            (a, b) -> Integer.compare(heuristic.evaluate(a), heuristic.evaluate(b))
        );
        Set<String> closedSet = new HashSet<>();
        
        openSet.add(initialState);
        
        while (!openSet.isEmpty()) {
            GameState current = openSet.poll();
            nodesVisited++;
            
            // Check if goal state reached
            if (current.getBoard().isWin()) {
                stopTimer();
                return current;
            }
            
            String boardState = current.getBoard().toString();
            if (closedSet.contains(boardState)) {
                continue;
            }
            
            closedSet.add(boardState);
            
            // Generate and enqueue possible moves
            for (Move move : current.getPossibleMoves()) {
                GameState nextState = current.applyMove(move);
                if (!closedSet.contains(nextState.getBoard().toString())) {
                    openSet.add(nextState);
                }
            }
        }
        
        stopTimer();
        return null;
    }
    
    @Override
    public String getName() {
        return "Greedy Best First Search";
    }
}