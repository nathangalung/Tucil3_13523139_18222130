package ui;

import core.GameState;
import java.awt.FlowLayout;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.Timer;


/**
 * Animates solution playback.
 */
public class Animation {
    private final BoardPanel boardPanel;
    private final List<GameState> solutionPath;
    private final JTextArea logArea;
    private int animationDelayMillis;
    private Timer animationTimer;
    private final AtomicInteger currentStep = new AtomicInteger(0);

    private JLabel stepLabel;
    private JButton playButton, pauseButton, resetButton;
    private JSlider speedSlider;

    public Animation(BoardPanel boardPanel, List<GameState> solutionPath, JTextArea logArea) {
        this.boardPanel = boardPanel;
        this.solutionPath = solutionPath;
        this.logArea = logArea;
        this.animationDelayMillis = 1000;
    }

    // Start animation playback.
    public void start() {
        if (playButton != null && playButton.isEnabled()) {
            playButton.doClick();
        } else if (solutionPath != null && !solutionPath.isEmpty()){
            setupAndPlayAnimation();
        }
    }
    
    // Create animation controls.
    public JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        int totalSteps = solutionPath.size() > 0 ? solutionPath.size() - 1 : 0;
        stepLabel = new JLabel("Step: 0/" + totalSteps);
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");
        speedSlider = new JSlider(100, 2000, 2100 - animationDelayMillis); 
        
        pauseButton.setEnabled(false);
        playButton.setEnabled(totalSteps > 0);
        resetButton.setEnabled(false);

        playButton.addActionListener(e -> setupAndPlayAnimation());
        pauseButton.addActionListener(e -> pauseAnimation());
        resetButton.addActionListener(e -> resetAnimation());
        speedSlider.addChangeListener(e -> {
            animationDelayMillis = 2100 - speedSlider.getValue(); 
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.setDelay(animationDelayMillis);
            }
        });

        panel.add(stepLabel);
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(resetButton);
        panel.add(new JLabel("Speed:"));
        panel.add(speedSlider);
        return panel;
    }

    // Setup and play/resume.
    private void setupAndPlayAnimation() {
        if (solutionPath == null || solutionPath.isEmpty()) return;
        
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(true);

        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        
        animationTimer = new Timer(animationDelayMillis, evt -> {
            int step = currentStep.get();
            if (step < solutionPath.size() -1) {
                step = currentStep.incrementAndGet();
                GameState state = solutionPath.get(step);
                boardPanel.updateBoard(state.getBoard());
                stepLabel.setText("Step: " + step + "/" + (solutionPath.size() - 1));
                logArea.append("Langkah " + step + ": " + state.getLastMove() + "\n");
                
                if (step == solutionPath.size() - 1) { 
                    animationTimer.stop();
                    playButton.setEnabled(false); 
                    pauseButton.setEnabled(false);
                }
            } else { 
                 animationTimer.stop();
                 playButton.setEnabled(false);
                 pauseButton.setEnabled(false);
            }
        });
       
        if (currentStep.get() == 0 && !solutionPath.isEmpty()) {
            boardPanel.updateBoard(solutionPath.get(0).getBoard());
            logArea.append("Papan Awal (Langkah 0)\n");
        }
        animationTimer.start();
    }

    // Pause the animation.
    private void pauseAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        playButton.setEnabled(currentStep.get() < solutionPath.size() -1); 
        pauseButton.setEnabled(false);
    }

    // Reset animation to start.
    private void resetAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        currentStep.set(0);
        if (solutionPath != null && !solutionPath.isEmpty()) {
            boardPanel.updateBoard(solutionPath.get(0).getBoard());
            stepLabel.setText("Step: 0/" + (solutionPath.size() - 1));
        } else {
            stepLabel.setText("Step: 0/0");
        }
        logArea.setText("");
        playButton.setEnabled(solutionPath != null && solutionPath.size() > 1);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false); 
    }
}