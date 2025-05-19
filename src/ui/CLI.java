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
import core.Piece;
import heuristic.BP;
import heuristic.DB;
import heuristic.Heuristic;
import heuristic.MD;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Command-line interface.
 */
public class CLI {
    private static final String ANSI_RESET  = "\u001B[0m";
    private static final String ANSI_RED    = "\u001B[31m";
    private static final String ANSI_GREEN  = "\u001B[32m"; 
    private static final String ANSI_YELLOW = "\u001B[33m"; 

    // Main entry for CLI.
    public static void main(String[] args) {
        CLI.run();
    }

    // Run the CLI application.
    public static void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter puzzle file name: ");
            String fileName = scanner.nextLine().trim();
            String filePath = resolveFilePath(fileName);

            Board board = new FileParser(filePath).parseFile();
            GameState initialState = new GameState(board);

            System.out.println("\nSelect algorithm:");
            System.out.println("1. UCS  2. GBFS  3. A*  4. IDAS");
            System.out.print("Choice: ");
            int algoChoice = scanner.nextInt();

            Heuristic heuristic = null;
            if (algoChoice > 1 && algoChoice <= 4) {
                System.out.println("\nSelect heuristic:");
                System.out.println("1. Manhattan Distance  2. Blocking Pieces  3. Distance+Blocking");
                System.out.print("Choice: ");
                heuristic = switch (scanner.nextInt()) {
                    case 2 -> new BP();
                    case 3 -> new DB();
                    default -> new MD();
                };
            }

            System.out.println("\nInitial Board:");
            printBoard(board, '\0'); 

            GameState solutionState;
            int nodes = 0;
            long timeMs = 0;
            String algoName = "";

            if (algoChoice == 1) {
                UCS solver = new UCS();
                solutionState = solver.solve(initialState);
                nodes = solver.getNodesVisited();
                timeMs = solver.getExecutionTime();
                algoName = solver.getName();
            } else if (algoChoice >= 2 && algoChoice <= 4) { 
                PathFinder solver = switch (algoChoice) {
                    case 2 -> new GBFS(heuristic);
                    case 3 -> new AS(heuristic);
                    case 4 -> new IDAS(heuristic);
                    default -> throw new IllegalArgumentException("Invalid algorithm choice.");
                };
                solutionState = solver.findPath(initialState);
                nodes = solver.getNodesVisited();
                timeMs = solver.getExecutionTime();
                algoName = solver.getName();
            } else {
                System.out.println("Invalid algorithm choice.");
                return;
            }

            if (solutionState != null) {
                List<GameState> path = solutionState.getSolutionPath();
                System.out.println("\nSolution: " + (path.size() - 1) + " moves.");
                for (int i = 1; i < path.size(); i++) { 
                    GameState state = path.get(i);
                    Move move = state.getLastMove();
                    System.out.println("\nMove " + i + ": " + move);
                    printBoard(state.getBoard(), move.getPiece().getId());
                }
                System.out.println("\n--- Statistics ---");
                System.out.println("Algorithm: " + algoName);
                if (heuristic != null) System.out.println("Heuristic: " + heuristic.getName());
                System.out.println("Nodes visited: " + nodes);
                System.out.println("Time: " + timeMs + " ms");
            } else {
                System.out.println("\nNo solution found.");
                 System.out.println("Nodes visited: " + nodes);
                System.out.println("Time: " + timeMs + " ms");
            }

        } catch (IOException e) {
            System.err.println("File Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Resolve file path.
    private static String resolveFilePath(String fileName) {
        if (fileName.contains(File.separator)) return fileName;
        if (!fileName.toLowerCase().endsWith(".txt")) fileName += ".txt";
        
        File fileInTestDir = new File("test", fileName);
        if (fileInTestDir.exists()) return fileInTestDir.getAbsolutePath();
        
        File fileInCurrentTestDir = new File(System.getProperty("user.dir"), "test" + File.separator + fileName);
         if (fileInCurrentTestDir.exists()) return fileInCurrentTestDir.getAbsolutePath();

        return fileName;
    }

    // Print board with colors.
    private static void printBoard(Board board, char movedPieceId) {
        char[][] grid = board.getGrid();
        int rows = board.getRows(), cols = board.getCols();
        Piece primary = board.getPrimaryPiece();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == '.') System.out.print('.');
                else if (primary != null && c == primary.getId()) System.out.print(ANSI_RED + c + ANSI_RESET);
                else if (movedPieceId != '\0' && c == movedPieceId) System.out.print(ANSI_YELLOW + c + ANSI_RESET);
                else System.out.print(c);
            }

            if (board.getExitRow() == i && board.getExitCol() == cols) {
                System.out.print(" " + ANSI_GREEN + 'K' + ANSI_RESET);
            }
            System.out.println();
        }
        if (board.getExitRow() == rows) {
            for(int j=0; j<board.getExitCol(); ++j) System.out.print(" "); 
            System.out.println(ANSI_GREEN + 'K' + ANSI_RESET);
        }
    }
}