package algorithm;

import core.GameState;
import core.Move;
import heuristic.Heuristic;

import java.util.*;

/**
 * A* search algorithm
 */
public class AS extends PathFinder {
    
    public AS(Heuristic heuristic) {
        super(heuristic);
    }
    
    @Override
    public GameState findPath(GameState initialState) {
        startTimer();
        
        // Priority queue based on f(n) = g(n) + h(n)
        PriorityQueue<GameState> openSet = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                a.getCost() + heuristic.evaluate(a),
                b.getCost() + heuristic.evaluate(b)
            )
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
        return "A* Search";
    }
}