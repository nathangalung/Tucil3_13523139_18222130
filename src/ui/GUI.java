package ui;

import algorithm.AS;
import algorithm.GBFS;
import algorithm.IDAS;
import algorithm.PathFinder;
import algorithm.UCS;
import core.Board;
import core.FileParser;
import core.GameState;
import heuristic.BP;
import heuristic.DB;
import heuristic.Heuristic;
import heuristic.MD;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Main graphical user interface.
 */
public class GUI extends JFrame {
    private BoardPanel boardPanel;
    private JTextArea logArea;
    private JPanel solutionDisplayArea;
    private JButton solveButton;
    private JComboBox<String> algoSelector;
    private JComboBox<String> heuristicSelector;
    private JLabel currentFileLabel;
    private Board currentBoard;
    private String currentPuzzleName;  // Store current puzzle name for screenshots

    public GUI() {
        setTitle("Rush Hour Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        setupControlPanel();
        setupSolutionDisplayArea();
        setupLogArea();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Setup top control panel.
    private void setupControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JButton fileButton = new JButton("Select Puzzle File");
        currentFileLabel = new JLabel("No file selected");
        fileButton.addActionListener(_ -> loadPuzzleFile());

        algoSelector = new JComboBox<>(new String[]{
            "Uniform Cost Search", "Greedy Best-First", "A* Search", "Iterative Deepening A*"
        });
        heuristicSelector = new JComboBox<>(new String[]{
            "Manhattan Distance", "Blocking Pieces", "Distance + Blocking"
        });
        algoSelector.addActionListener(_ -> heuristicSelector.setEnabled(algoSelector.getSelectedIndex() != 0));
        heuristicSelector.setEnabled(algoSelector.getSelectedIndex() != 0);

        solveButton = new JButton("Solve");
        solveButton.addActionListener(_ -> solvePuzzle());
        solveButton.setEnabled(false);

        panel.add(fileButton);
        panel.add(currentFileLabel);
        panel.add(new JLabel("Algorithm:"));
        panel.add(algoSelector);
        panel.add(new JLabel("Heuristic:"));
        panel.add(heuristicSelector);
        panel.add(solveButton);
        add(panel, BorderLayout.NORTH);
    }

    // Setup main board display.
    private void setupSolutionDisplayArea() {
        solutionDisplayArea = new JPanel(new BorderLayout());
        boardPanel = new BoardPanel(null); 
        solutionDisplayArea.add(boardPanel, BorderLayout.CENTER);
        add(solutionDisplayArea, BorderLayout.CENTER);
    }

    // Setup bottom log area.
    private void setupLogArea() {
        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        JLabel footer = new JLabel("© 2025 IF2211_TK3_13523139_18222130", SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        bottomPanel.add(footer, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Load puzzle from file.
    private void loadPuzzleFile() {
        File testDir = new File(System.getProperty("user.dir"), "test/input");
        JFileChooser chooser = new JFileChooser(testDir.exists() ? testDir : null);
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String fileName = file.getName();
            currentFileLabel.setText(fileName);
            currentPuzzleName = fileName.replace(".txt", "");
            
            try {
                currentBoard = new FileParser(file.getAbsolutePath()).parseFile();
                displayBoard(currentBoard);
                logArea.setText("Puzzle loaded: " + fileName + "\n");
                solveButton.setEnabled(true);

                // Clear previous solution animation controls
                if (solutionDisplayArea.getComponentCount() > 1) {
                    Component lastComp = solutionDisplayArea.getComponent(solutionDisplayArea.getComponentCount() -1);
                    if (!(lastComp instanceof BoardPanel)) { 
                       solutionDisplayArea.remove(lastComp);
                    }
                }
                // After removing old controls, revalidate and repaint the container
                solutionDisplayArea.revalidate();
                solutionDisplayArea.repaint();
                
                // Take screenshot of initial state
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Small delay to ensure UI is fully drawn
                        Thread.sleep(200);
                        takeScreenshot("initial");
                    } catch (InterruptedException ex) {
                        logArea.append("Failed to capture initial state screenshot: " + ex.getMessage() + "\n");
                    }
                });
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "File Load Error", JOptionPane.ERROR_MESSAGE);
                logArea.setText("Error loading " + fileName + ": " + ex.getMessage() + "\n");
                currentBoard = null;
                solveButton.setEnabled(false);
            }
        }
    }
    
    // Update board display.
    private void displayBoard(Board boardToDisplay) {
        if (boardPanel == null) {
            boardPanel = new BoardPanel(boardToDisplay);
            solutionDisplayArea.add(boardPanel, BorderLayout.CENTER);
        } else {
            boardPanel.updateBoard(boardToDisplay);
        }
        pack();
    }

    // Solve the current puzzle.
    private void solvePuzzle() {
        if (currentBoard == null) {
            JOptionPane.showMessageDialog(this, "Load puzzle first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        solveButton.setEnabled(false);
        logArea.setText("Solving puzzle...\n");

        int algoIdx = algoSelector.getSelectedIndex();
        Heuristic selectedHeuristic = switch (heuristicSelector.getSelectedIndex()) {
            case 1 -> new BP();
            case 2 -> new DB();
            default -> new MD();
        };

        new Thread(() -> {
            GameState initial = new GameState(currentBoard.copy());
            GameState solutionState;
            int nodes;
            long timeMs;
            String algoName = algoSelector.getSelectedItem().toString();
            String heurName = heuristicSelector.getSelectedItem().toString();

            try {
                if (algoIdx == 0) {
                    UCS solver = new UCS();
                    solutionState = solver.solve(initial);
                    nodes = solver.getNodesVisited();
                    timeMs = solver.getExecutionTime();
                } else {
                    PathFinder solver = switch (algoIdx) {
                        case 1 -> new GBFS(selectedHeuristic);
                        case 2 -> new AS(selectedHeuristic);
                        case 3 -> new IDAS(selectedHeuristic);
                        default -> throw new IllegalStateException("Invalid algorithm index.");
                    };
                    solutionState = solver.findPath(initial);
                    nodes = solver.getNodesVisited();
                    timeMs = solver.getExecutionTime();
                }
            } catch (IllegalStateException ex) {
                 final String errorMsg = "Solver error: " + ex.getMessage();
                 SwingUtilities.invokeLater(() -> {
                    logArea.append(errorMsg + "\n");
                    solveButton.setEnabled(true);
                 });
                 return;
            }

            final GameState finalSolution = solutionState;
            final int finalNodes = nodes;
            final long finalTimeMs = timeMs;
            final String finalHeurName = (algoIdx != 0) ? heurName : null;
            final String finalAlgoName = algoName.replaceAll("\\s+", "");

            SwingUtilities.invokeLater(() -> {
                if (finalSolution != null) {
                    List<GameState> path = finalSolution.getSolutionPath();
                    displaySolutionAnimation(path);
                    logArea.append("Solution: " + (path.size() - 1) + " moves.\n");
                    
                    // Take screenshot of final state
                    new Thread(() -> {
                        try {
                            // Jump to the last state in the animation
                            boardPanel.updateBoard(path.get(path.size() - 1).getBoard());
                            Thread.sleep(500);
                            takeScreenshot("final_" + finalAlgoName);
                        } catch (InterruptedException ex) {
                            SwingUtilities.invokeLater(() -> 
                                logArea.append("Failed to capture final state screenshot: " + ex.getMessage() + "\n")
                            );
                        }
                    }).start();
                } else {
                    logArea.append("No solution found.\n");
                }
                logArea.append("Algorithm: " + finalAlgoName + "\n");
                if (finalHeurName != null) logArea.append("Heuristic: " + finalHeurName + "\n");
                logArea.append("Nodes visited: " + finalNodes + "\n");
                logArea.append("Time: " + finalTimeMs + " ms\n");
                solveButton.setEnabled(true);
            });
        }).start();
    }

    // Display solution animation.
    private void displaySolutionAnimation(List<GameState> solutionPath) {
        if (solutionPath == null || solutionPath.isEmpty()) return;
        
        boardPanel.updateBoard(solutionPath.get(0).getBoard());
        Animation animation = new Animation(boardPanel, solutionPath, logArea);
        JPanel animControls = animation.createControlPanel();

        if (solutionDisplayArea.getComponentCount() > 1) {
             Component lastComp = solutionDisplayArea.getComponent(solutionDisplayArea.getComponentCount() -1);
             if (!(lastComp instanceof BoardPanel)) {
                solutionDisplayArea.remove(lastComp);
             }
        }
        solutionDisplayArea.add(animControls, BorderLayout.SOUTH);
        solutionDisplayArea.revalidate();
        solutionDisplayArea.repaint();
        pack();
        animation.start();
    }

    // Take screenshot of the application window
    private void takeScreenshot(String state) {
        try {
            // Get current algorithm and heuristic names
            String algoShortName;
            String heurShortName;
            String baseDir = "test" + File.separator + "output";
            String targetDir;
            
            if (state.contains("final")) {
                // Get algorithm short name
                int algoIdx = algoSelector.getSelectedIndex();
                switch (algoIdx) {
                    case 0 -> algoShortName = "UCS";
                    case 1 -> algoShortName = "GBFS";
                    case 2 -> algoShortName = "AS";
                    case 3 -> algoShortName = "IDAS";
                    default -> algoShortName = "Unknown";
                }
                
                // Get heuristic short name
                if (algoIdx != 0) {
                    int heurIdx = heuristicSelector.getSelectedIndex();
                    switch (heurIdx) {
                        case 0 -> heurShortName = "MD";
                        case 1 -> heurShortName = "BP";
                        case 2 -> heurShortName = "DB";
                        default -> heurShortName = "Unknown";
                    }
                    
                    // For final state
                    targetDir = baseDir + File.separator + "final" + 
                                File.separator + algoShortName + 
                                File.separator + heurShortName;
                } else {
                    // For UCS with no heuristic
                    targetDir = baseDir + File.separator + "final" + 
                                File.separator + algoShortName;
                }
            } else {
                // For initial state
                targetDir = baseDir + File.separator + "initial";
            }
            
            // Create the directories if they don't exist
            File outputDir = new File(targetDir);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            String filename = targetDir + File.separator + currentPuzzleName + ".png";
            
            // Capture only the board panel
            BufferedImage screenshot;
            if (boardPanel != null) {
                screenshot = new BufferedImage(
                    boardPanel.getWidth(), 
                    boardPanel.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
                );
                
                boardPanel.paint(screenshot.getGraphics());
            } else {
                // Fallback
                screenshot = new Robot().createScreenCapture(getBounds());
            }
            
            // Save to file
            File outputFile = new File(filename);
            ImageIO.write(screenshot, "png", outputFile);
            logArea.append("Board screenshot saved: " + outputFile.getPath() + "\n");
            
        } catch (AWTException | IOException e) {
            logArea.append("Error taking screenshot: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}