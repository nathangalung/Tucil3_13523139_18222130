package algorithm;

import core.GameState;
import core.Move;
import heuristic.Heuristic;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Greedy Best-First Search.
 * Uses h(n) only.
 */
public class GBFS extends PathFinder {
    
    public GBFS(Heuristic heuristic) {
        super(heuristic);
    }
    
    @Override
    public GameState findPath(GameState initialState) {
        startTimer();
        
        PriorityQueue<GameState> openSet = new PriorityQueue<>(
            Comparator.comparingInt(heuristic::evaluate)
        );
        Set<String> closedSet = new HashSet<>(); 
        
        openSet.add(initialState);
        
        while (!openSet.isEmpty()) {
            GameState current = openSet.poll();
            nodesVisited++;
            
            if (current.getBoard().isWin()) {
                stopTimer();
                return current;
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
        return null; 
    }
    
    @Override
    public String getName() {
        return "Greedy Best-First Search";
    }
}