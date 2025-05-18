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
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Graphical user interface
 */
public class GUI extends JFrame {
    private BoardPanel boardPanel;
    private JTextArea logArea;
    private JPanel controlPanel;
    private JPanel solutionPanel;
    private JButton solveButton;
    private JComboBox<String> algoCombo;
    private JComboBox<String> heuristicCombo;

    public GUI() {
        setTitle("Rush Hour Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        controlPanel  = createControlPanel();
        solutionPanel = new JPanel(new BorderLayout());
        logArea       = new JTextArea(8, 40);
        logArea.setEditable(false);

        add(controlPanel,  BorderLayout.NORTH);
        add(solutionPanel, BorderLayout.CENTER);

        // bottom area with log and footer
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        JLabel footer = new JLabel("© 2025 Rush Hour Solver", SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        bottomPanel.add(footer, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton fileButton = new JButton("Select Puzzle File");
        JLabel fileLabel   = new JLabel("No file selected");
        fileButton.addActionListener(e -> {
            File testDir = new File("test");
            if (!testDir.exists()) testDir = new File(System.getProperty("user.dir"), "test");
            JFileChooser chooser = new JFileChooser(testDir);
            chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                fileLabel.setText(f.getName());
                try {
                    Board board = new FileParser(f.getAbsolutePath()).parseFile();
                    displayBoard(board);
                    logArea.setText("Puzzle loaded: " + f.getName() + "\n");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error loading file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    logArea.setText("Error: " + ex.getMessage() + "\n");
                }
            }
        });

        algoCombo = new JComboBox<>(new String[]{
            "Uniform Cost Search (UCS)",
            "Greedy Best First Search (GBFS)",
            "A* Search (AS)",
            "Iterative Deepening A* (IDAS)"
        });
        heuristicCombo = new JComboBox<>(new String[]{
            "Manhattan Distance (MD)",
            "Blocking Pieces (BP)",
            "Distance + Blocking (DB)"
        });
        algoCombo.addActionListener(e -> {
            boolean ucs = algoCombo.getSelectedIndex() == 0;
            heuristicCombo.setEnabled(!ucs);
        });
        heuristicCombo.setEnabled(algoCombo.getSelectedIndex() != 0);

        solveButton = new JButton("Solve");
        solveButton.addActionListener(e -> onSolve());

        panel.add(fileButton);
        panel.add(fileLabel);
        panel.add(new JLabel("Algorithm:"));
        panel.add(algoCombo);
        panel.add(new JLabel("Heuristic:"));
        panel.add(heuristicCombo);
        panel.add(solveButton);

        return panel;
    }

    private void onSolve() {
        if (boardPanel == null) {
            JOptionPane.showMessageDialog(this,
                "Please load a puzzle first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int algoIndex      = algoCombo.getSelectedIndex();
        int heurIndex      = heuristicCombo.getSelectedIndex();
        Heuristic heuristic = switch (heurIndex) {
            case 1 -> new BP();
            case 2 -> new DB();
            default -> new MD();
        };

        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                solveButton.setEnabled(false);
                logArea.setText("Solving puzzle...\n");
            });

            GameState start = new GameState(boardPanel.getBoard());
            List<GameState> path;
            int visited;
            long time;
            String algoName;
            String heurName = null;

            if (algoIndex == 0) {
                UCS solver = new UCS();
                GameState sol = solver.solve(start);
                path    = sol.getSolutionPath();
                visited = solver.getNodesVisited();
                time    = solver.getExecutionTime();
                algoName = "Uniform Cost Search";
            } else {
                PathFinder solver = switch (algoIndex) {
                    case 1 -> new GBFS(heuristic);
                    case 2 -> new AS(heuristic);
                    default -> new IDAS(heuristic);
                };
                GameState sol = solver.findPath(start);
                path    = sol.getSolutionPath();
                visited = solver.getNodesVisited();
                time    = solver.getExecutionTime();
                algoName = solver.getName();
                heurName = heuristic.getName();
            }

            final List<GameState> solutionPath = path;
            final int    nodesCount = visited;
            final long   execTime   = time;
            final String finalAlgo = algoName;
            final String finalHeu  = heurName;

            SwingUtilities.invokeLater(() -> {
                displaySolution(solutionPath);
                logArea.append("Solution found with " + (solutionPath.size()-1) + " moves\n");
                logArea.append("Algorithm: " + finalAlgo + "\n");
                if (finalHeu != null) logArea.append("Heuristic: " + finalHeu + "\n");
                logArea.append("Nodes visited: " + nodesCount + "\n");
                logArea.append("Execution time: " + execTime + " ms\n");
                solveButton.setEnabled(true);
            });
        }).start();
    }

    private void displayBoard(Board board) {
        if (boardPanel != null) solutionPanel.remove(boardPanel);
        boardPanel = new BoardPanel(board);
        solutionPanel.add(boardPanel, BorderLayout.CENTER);
        solutionPanel.revalidate(); solutionPanel.repaint(); pack();
    }

    private void displaySolution(List<GameState> sol) {
        boardPanel.updateBoard(sol.get(0).getBoard());
        Animation animation = new Animation(boardPanel, sol, logArea);
        JPanel controls = animation.createControlPanel();
        for (Component c : solutionPanel.getComponents()) {
            if (c != boardPanel) solutionPanel.remove(c);
        }
        solutionPanel.add(controls, BorderLayout.SOUTH);
        solutionPanel.revalidate(); solutionPanel.repaint(); pack();
        animation.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
