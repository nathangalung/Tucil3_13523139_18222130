// filepath: c:\Users\NathanGalung\Documents\Kuliah\sem6\stima\Tucil\IF2211_TK3_13523139_18222130\src\heuristic\Heuristic.java
package heuristic;

import core.GameState;

/**
 * Abstract base for heuristics.
 */
public abstract class Heuristic {
    // Evaluate game state.
    public abstract int evaluate(GameState state);
    
    // Get heuristic name.
    public abstract String getName();
}