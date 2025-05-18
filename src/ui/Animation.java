package ui;

import core.GameState;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

/**
 * Animated solution playback with GUI logging
 */
public class Animation {
    private BoardPanel boardPanel;
    private List<GameState> solution;
    private JTextArea logArea;
    private int delay;
    private Timer timer;
    private JLabel stepLabel;
    private JButton playButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JSlider speedSlider;

    public Animation(BoardPanel boardPanel, List<GameState> solution, JTextArea logArea) {
        this.boardPanel = boardPanel;
        this.solution = solution;
        this.logArea = logArea;
        this.delay = 1000; // Default delay
    }

    /**
     * Start animation as if Play was pressed
     */
    public void start() {
        if (playButton != null) {
            playButton.doClick();
        }
    }

    /**
     * Create controls and setup animation callbacks
     */
    public JPanel createControlPanel() {
        JPanel panel = new JPanel();
        stepLabel = new JLabel("Step: 0/" + (solution.size() - 1));
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");
        speedSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, delay);
        pauseButton.setEnabled(false);

        AtomicInteger currentStep = new AtomicInteger(0);

        playButton.addActionListener(e -> {
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
            resetButton.setEnabled(true);
            if (timer != null && timer.isRunning()) timer.stop();
            timer = new Timer(delay, evt -> {
                int step = currentStep.incrementAndGet();
                if (step < solution.size()) {
                    GameState state = solution.get(step);
                    boardPanel.updateBoard(state.getBoard());
                    stepLabel.setText("Step: " + step + "/" + (solution.size() - 1));
                    // Log step inside GUI
                    logArea.append("Langkah " + step + ": " + state.getLastMove() + "\n");
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
            if (timer != null && timer.isRunning()) timer.stop();
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
        });

        resetButton.addActionListener(e -> {
            if (timer != null && timer.isRunning()) timer.stop();
            currentStep.set(0);
            boardPanel.updateBoard(solution.get(0).getBoard());
            stepLabel.setText("Step: 0/" + (solution.size() - 1));
            logArea.setText("");
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
        });

        speedSlider.addChangeListener(e -> {
            delay = 2100 - speedSlider.getValue();
            if (timer != null && timer.isRunning()) timer.setDelay(delay);
        });

        panel.add(stepLabel);
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(resetButton);
        panel.add(new JLabel("Speed:"));
        panel.add(speedSlider);
        return panel;
    }
}
