package ui;

import algorithm.PathFinder;
import core.*;
import heuristic.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Command-line interface
 */
public class CLI {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    
    /**
     * Run CLI solver
     */
    public static void run(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Get the puzzle file
            System.out.print("Enter the puzzle file name: ");
            String fileName = scanner.nextLine().trim();
            
            // Handle file path construction
            String filePath = getFilePath(fileName);
            
            FileParser parser = new FileParser(filePath);
            Board board = parser.parseFile();
            GameState initialState = new GameState(board);
            
            // Select the algorithm
            System.out.println("Select the algorithm:");
            System.out.println("1. Uniform Cost Search (UCS)");
            System.out.println("2. Greedy Best First Search (GBFS)");
            System.out.println("3. A* Search (AS)");
            System.out.println("4. Iterative Deepening A* (IDAS)");
            System.out.print("Enter your choice: ");
            int algoChoice = scanner.nextInt();
            
            // Select the heuristic
            System.out.println("Select the heuristic:");
            System.out.println("1. Manhattan Distance (MD)");
            System.out.println("2. Blocking Pieces (BP)");
            System.out.println("3. Distance + Blocking (DB)");
            System.out.print("Enter your choice: ");
            int heurChoice = scanner.nextInt();
            
            // Create the heuristic
            Heuristic heuristic;
            switch (heurChoice) {
                case 1:
                    heuristic = new MD();
                    break;
                case 2:
                    heuristic = new BP();
                    break;
                case 3:
                    heuristic = new DB();
                    break;
                default:
                    heuristic = new MD();
            }
            
            // Create and run the algorithm
            PathFinder pathFinder;
            switch (algoChoice) {
                case 1:
                    pathFinder = new algorithm.UCS(heuristic);
                    break;
                case 2:
                    pathFinder = new algorithm.GBFS(heuristic);
                    break;
                case 3:
                    pathFinder = new algorithm.AS(heuristic);
                    break;
                case 4:
                    pathFinder = new algorithm.IDAS(heuristic);
                    break;
                default:
                    pathFinder = new algorithm.AS(heuristic);
            }
            
            // Display initial board
            System.out.println("\nPapan Awal");
            printBoard(board, (char)0);
            
            // Run the algorithm
            GameState solution = pathFinder.findPath(initialState);
            
            if (solution != null) {
                // Print the solution
                List<GameState> path = solution.getSolutionPath();
                
                System.out.println("\nSolusi ditemukan dengan " + (path.size() - 1) + " gerakan:");
                
                for (int i = 1; i < path.size(); i++) {
                    GameState state = path.get(i);
                    Move move = state.getLastMove();
                    
                    System.out.println("\nGerakan " + i + ": " + move);
                    printBoard(state.getBoard(), move.getPiece().getId());
                }
                
                // Print statistics
                System.out.println("\nStatistik:");
                System.out.println("Algoritma: " + pathFinder.getName());
                System.out.println("Heuristic: " + heuristic.getName());
                System.out.println("Jumlah node yang diperiksa: " + pathFinder.getNodesVisited());
                System.out.println("Waktu eksekusi: " + pathFinder.getExecutionTime() + " ms");
            } else {
                System.out.println("\nTidak ditemukan solusi!");
            }
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Helper for file path handling
     */
    private static String getFilePath(String fileName) {
        // If it's a full path, use it directly
        if (fileName.contains("/") || fileName.contains("\\")) {
            return fileName;
        }
        
        // Add .txt extension if missing
        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName = fileName + ".txt";
        }
        
        // Check in the test directory first
        File testFile = new File("test" + File.separator + fileName);
        if (testFile.exists()) {
            return testFile.getAbsolutePath();
        }
        
        // Try current directory with test subfolder
        testFile = new File(System.getProperty("user.dir"), "test" + File.separator + fileName);
        if (testFile.exists()) {
            return testFile.getAbsolutePath();
        }
        
        return fileName;
    }
    
    /**
     * Print board with colored pieces
     */
    private static void printBoard(Board board, char movedPiece) {
        char[][] grid = board.getGrid();
        int rows = board.getRows();
        int cols = board.getCols();
        Piece primary = board.getPrimaryPiece();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                
                if (cell == '.') {
                    System.out.print(".");
                } else if (primary != null && cell == primary.getId()) {
                    // Highlight primary piece
                    System.out.print(ANSI_RED + cell + ANSI_RESET);
                } else if (movedPiece != 0 && cell == movedPiece) {
                    // Highlight moved piece
                    System.out.print(ANSI_YELLOW + cell + ANSI_RESET);
                } else {
                    System.out.print(cell);
                }
            }
            System.out.println();
        }
        
        // Mark the exit
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        if (exitRow >= 0 && exitCol >= 0) {
            System.out.println("Exit at: (" + exitRow + ", " + exitCol + ") " + ANSI_GREEN + "K" + ANSI_RESET);
        }
    }
}