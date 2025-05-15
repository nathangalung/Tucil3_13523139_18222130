package ui;

import algorithm.*;
import core.*;
import heuristic.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Graphical user interface
 */
public class GUI extends JFrame {
    private BoardPanel boardPanel;
    private JTextArea logArea;
    private JPanel controlPanel;
    private JPanel solutionPanel;
    
    /**
     * Initialize GUI window
     */
    public GUI() {
        setTitle("Rush Hour Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panels
        controlPanel = createControlPanel();
        solutionPanel = new JPanel(new BorderLayout());
        
        // Create log area
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        
        // Add components to frame
        add(controlPanel, BorderLayout.NORTH);
        add(solutionPanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Create algorithm control panel
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // File selection
        JButton fileButton = new JButton("Select Puzzle File");
        JLabel fileLabel = new JLabel("No file selected");
        
        // Algorithm selection
        JComboBox<String> algoCombo = new JComboBox<>(new String[] {
            "Uniform Cost Search (UCS)",
            "Greedy Best First Search (GBFS)",
            "A* Search (AS)",
            "Iterative Deepening A* (IDAS)"
        });
        
        // Heuristic selection
        JComboBox<String> heuristicCombo = new JComboBox<>(new String[] {
            "Manhattan Distance (MD)",
            "Blocking Pieces (BP)",
            "Distance + Blocking (DB)"
        });
        
        // Solve button
        JButton solveButton = new JButton("Solve");
        
        // Add action to file button
        fileButton.addActionListener(e -> {
            // Set the initial directory to the test folder
            File testDir = new File("test");
            if (!testDir.exists()) {
                testDir = new File(System.getProperty("user.dir"), "test");
            }
            
            JFileChooser fileChooser = new JFileChooser(testDir);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            int result = fileChooser.showOpenDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                fileLabel.setText(selectedFile.getName());
                
                try {
                    // Parse and display the initial board
                    FileParser parser = new FileParser(selectedFile.getAbsolutePath());
                    Board board = parser.parseFile();
                    displayBoard(board);
                    logArea.setText("Puzzle loaded: " + selectedFile.getName() + "\n");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    logArea.setText("Error: " + ex.getMessage() + "\n");
                }
            }
        });
        
        // Add action to solve button
        solveButton.addActionListener(e -> {
            if (boardPanel == null) {
                JOptionPane.showMessageDialog(this, "Please load a puzzle first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get selected algorithm and heuristic
            int algoIndex = algoCombo.getSelectedIndex();
            int heuristicIndex = heuristicCombo.getSelectedIndex();
            
            // Create heuristic
            Heuristic heuristic;
            switch (heuristicIndex) {
                case 0: heuristic = new MD(); break;
                case 1: heuristic = new BP(); break;
                case 2: heuristic = new DB(); break;
                default: heuristic = new MD();
            }
            
            // Create algorithm
            PathFinder pathFinder;
            switch (algoIndex) {
                case 0: pathFinder = new UCS(heuristic); break;
                case 1: pathFinder = new GBFS(heuristic); break;
                case 2: pathFinder = new AS(heuristic); break;
                case 3: pathFinder = new IDAS(heuristic); break;
                default: pathFinder = new AS(heuristic);
            }
            
            // Start solving in a background thread
            new Thread(() -> {
                try {
                    // Disable controls during solving
                    SwingUtilities.invokeLater(() -> {
                        solveButton.setEnabled(false);
                        logArea.setText("Solving puzzle...\n");
                    });
                    
                    // Get the current board
                    Board currentBoard = boardPanel.getBoard();
                    GameState initialState = new GameState(currentBoard);
                    
                    // Find solution
                    GameState solution = pathFinder.findPath(initialState);
                    
                    // Display results
                    SwingUtilities.invokeLater(() -> {
                        if (solution != null) {
                            List<GameState> path = solution.getSolutionPath();
                            displaySolution(path);
                            
                            logArea.append("Solution found with " + (path.size() - 1) + " moves!\n");
                            logArea.append("Algorithm: " + pathFinder.getName() + "\n");
                            logArea.append("Heuristic: " + heuristic.getName() + "\n");
                            logArea.append("Nodes visited: " + pathFinder.getNodesVisited() + "\n");
                            logArea.append("Execution time: " + pathFinder.getExecutionTime() + " ms\n");
                        } else {
                            logArea.append("No solution found!\n");
                        }
                        solveButton.setEnabled(true);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        logArea.append("Error: " + ex.getMessage() + "\n");
                        solveButton.setEnabled(true);
                    });
                }
            }).start();
        });
        
        // Add components to panel
        panel.add(fileButton);
        panel.add(fileLabel);
        panel.add(new JLabel("Algorithm:"));
        panel.add(algoCombo);
        panel.add(new JLabel("Heuristic:"));
        panel.add(heuristicCombo);
        panel.add(solveButton);
        
        return panel;
    }
    
    // Rest of the code remains the same
    /**
     * Display board in the UI
     */
    private void displayBoard(Board board) {
        // Remove the existing board panel if it exists
        if (boardPanel != null) {
            solutionPanel.remove(boardPanel);
        }
        
        // Create new board panel
        boardPanel = new BoardPanel(board);
        solutionPanel.add(boardPanel, BorderLayout.CENTER);
        
        // Update UI
        solutionPanel.revalidate();
        solutionPanel.repaint();
        pack();
    }
    
    /**
     * Display solution animation
     */
    private void displaySolution(List<GameState> solution) {
        // Update the board panel to show the initial state
        boardPanel.updateBoard(solution.get(0).getBoard());
        
        // Create animation controls
        Animation animation = new Animation(boardPanel, solution);
        JPanel animationControls = animation.createControlPanel();
        
        // Replace any existing animation controls
        Component[] components = solutionPanel.getComponents();
        for (Component c : components) {
            if (c instanceof JPanel && c != boardPanel) {
                solutionPanel.remove(c);
            }
        }
        
        // Add the animation controls
        solutionPanel.add(animationControls, BorderLayout.SOUTH);
        solutionPanel.revalidate();
        solutionPanel.repaint();
    }
    
    /**
     * Get the board panel
     */
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}