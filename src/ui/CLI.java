package ui;

import algorithm.AS;
import algorithm.GBFS;
import algorithm.IDAS;
import algorithm.PathFinder;
import algorithm.UCS;
import core.Board;
import core.FileParser;
import core.GameState;
import core.Move;
import heuristic.BP;
import heuristic.DB;
import heuristic.Heuristic;
import heuristic.MD;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Command-line interface
 */
public class CLI {
    private static final String ANSI_RESET  = "\u001B[0m";
    private static final String ANSI_RED    = "\u001B[31m";
    private static final String ANSI_GREEN  = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    /**
     * Entry point for CLI execution
     */
    public static void main(String[] args) {
        CLI.run(args);
    }

    /**
     * Run CLI solver
     */
    public static void run(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Puzzle file
            System.out.print("Enter the puzzle file name: ");
            String fileName = scanner.nextLine().trim();
            String filePath = getFilePath(fileName);

            // Parse board
            Board board = new FileParser(filePath).parseFile();
            GameState start = new GameState(board);

            // Algorithm selection
            System.out.println("Select the algorithm:");
            System.out.println("1. Uniform Cost Search (UCS)");
            System.out.println("2. Greedy Best-First Search (GBFS)");
            System.out.println("3. A* Search (AS)");
            System.out.println("4. Iterative Deepening A* (IDAS)");
            System.out.print("Enter your choice: ");
            int algoChoice = scanner.nextInt();

            // Heuristic only for non-UCS algorithms
            Heuristic heuristic = null;
            if (algoChoice != 1) {
                System.out.println("Select the heuristic:");
                System.out.println("1. Manhattan Distance (MD)");
                System.out.println("2. Blocking Pieces (BP)");
                System.out.println("3. Distance + Blocking (DB)");
                System.out.print("Enter your choice: ");
                int heurChoice = scanner.nextInt();
                switch (heurChoice) {
                    case 2 -> heuristic = new BP();
                    case 3 -> heuristic = new DB();
                    default -> heuristic = new MD();
                }
            } else {
                System.out.println("[UCS] heuristic ignored");
            }

            // Display initial board
            System.out.println("\nPapan Awal");
            printBoard(board, '\0');

            // Solve
            GameState solution;
            int nodesVisited;
            long execTime;

            if (algoChoice == 1) {
                // UCS does not use heuristic
                UCS solver = new UCS();
                solution    = solver.solve(start);
                nodesVisited= solver.getNodesVisited();
                execTime    = solver.getExecutionTime();
            } else {
                // GBFS, AS, IDAS use heuristic
                PathFinder solver;
                switch (algoChoice) {
                    case 2 -> solver = new GBFS(heuristic);
                    case 3 -> solver = new AS(heuristic);
                    case 4 -> solver = new IDAS(heuristic);
                    default -> solver = new AS(heuristic);
                }
                solution    = solver.findPath(start);
                nodesVisited= solver.getNodesVisited();
                execTime    = solver.getExecutionTime();
            }

            // Output results
            if (solution != null) {
                List<GameState> path = solution.getSolutionPath();
                System.out.println("\nSolusi ditemukan dengan " + (path.size() - 1) + " langkah:");
                for (int i = 1; i < path.size(); i++) {
                    GameState state = path.get(i);
                    Move move = state.getLastMove();
                    System.out.println("\nLangkah " + i + ": " + move);
                    printBoard(state.getBoard(), move.getPiece().getId());
                }
                System.out.println("\nStatistik:");
                System.out.println("Algoritma              : " +
                    (algoChoice == 1 ? "Uniform Cost Search" :
                     algoChoice == 2 ? "Greedy Best-First Search" :
                     algoChoice == 3 ? "A* Search" : "Iterative Deepening A*"));
                if (algoChoice != 1) {
                    System.out.println("Heuristic              : " + heuristic.getName());
                }
                System.out.println("Node visited           : " + nodesVisited);
                System.out.println("Waktu eksekusi (ms)    : " + execTime);
            } else {
                System.out.println("\nTidak ditemukan solusi!");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Helper file path
    private static String getFilePath(String fileName) {
        if (fileName.contains("/") || fileName.contains("\\")) return fileName;
        if (!fileName.toLowerCase().endsWith(".txt")) fileName += ".txt";
        File testFile = new File("test", fileName);
        if (testFile.exists()) return testFile.getAbsolutePath();
        testFile = new File(System.getProperty("user.dir"), "test" + File.separator + fileName);
        if (testFile.exists()) return testFile.getAbsolutePath();
        return fileName;
    }

    // Print board with ANSI colors
    private static void printBoard(Board board, char movedPiece) {
        char[][] grid = board.getGrid();
        int rows = board.getRows(), cols = board.getCols();
        var primary = board.getPrimaryPiece();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == '.') System.out.print('.');
                else if (primary != null && c == primary.getId()) System.out.print(ANSI_RED + c + ANSI_RESET);
                else if (movedPiece != '\0' && c == movedPiece) System.out.print(ANSI_YELLOW + c + ANSI_RESET);
                else System.out.print(c);
            }
            System.out.println();
        }
        int exitRow = board.getExitRow(), exitCol = board.getExitCol();
        if (exitRow >= 0 && exitCol >= 0) {
            System.out.println("Exit at: (" + exitRow + "," + exitCol + ") " + ANSI_GREEN + 'K' + ANSI_RESET);
        }
    }
}