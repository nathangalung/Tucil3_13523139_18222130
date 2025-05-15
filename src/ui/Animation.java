package ui;

import core.Board;
import core.GameState;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Animated solution playback
 */
public class Animation {
    private BoardPanel boardPanel;
    private List<GameState> solution;
    private int delay;
    private Timer timer;
    private JLabel stepLabel;
    private JButton playButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JSlider speedSlider;
    
    public Animation(BoardPanel boardPanel, List<GameState> solution) {
        this.boardPanel = boardPanel;
        this.solution = solution;
        this.delay = 1000; // Default delay in milliseconds
    }
    
    /**
     * Setup animation controls
     */
    public JPanel createControlPanel() {
        JPanel panel = new JPanel();
        
        stepLabel = new JLabel("Step: 0/" + (solution.size() - 1));
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");
        speedSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, delay);
        
        pauseButton.setEnabled(false);
        
        // Set up event handlers
        AtomicInteger currentStep = new AtomicInteger(0);
        
        playButton.addActionListener(e -> {
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
            resetButton.setEnabled(true);
            
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            
            timer = new Timer(delay, evt -> {
                int step = currentStep.incrementAndGet();
                if (step < solution.size()) {
                    GameState state = solution.get(step);
                    boardPanel.updateBoard(state.getBoard());
                    stepLabel.setText("Step: " + step + "/" + (solution.size() - 1));
                    
                    if (step == solution.size() - 1) {
                        timer.stop();
                        playButton.setEnabled(true);
                        pauseButton.setEnabled(false);
                    }
                }
            });
            
            timer.start();
        });
        
        pauseButton.addActionListener(e -> {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
        });
        
        resetButton.addActionListener(e -> {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            currentStep.set(0);
            GameState initialState = solution.get(0);
            boardPanel.updateBoard(initialState.getBoard());
            stepLabel.setText("Step: 0/" + (solution.size() - 1));
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
        });
        
        speedSlider.addChangeListener(e -> {
            delay = 2100 - speedSlider.getValue(); // Invert so sliding right makes it faster
            if (timer != null && timer.isRunning()) {
                timer.setDelay(delay);
            }
        });
        
        // Create a label for the slider
        JLabel speedLabel = new JLabel("Animation Speed:");
        
        // Add components to panel
        panel.add(stepLabel);
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(resetButton);
        panel.add(speedLabel);
        panel.add(speedSlider);
        
        return panel;
    }
}