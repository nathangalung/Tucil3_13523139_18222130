package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parses puzzle input files.
 */
public class FileParser {
    private final String filePath;
    
    public FileParser(String filePath) {
        this.filePath = filePath;
    }
    
    // Parse the puzzle file.
    public Board parseFile() throws IOException {
        List<String> lines = readAllLines();
        
        int lineIdx = 0;
        String dimLine = getNextNonCommentLine(lines, lineIdx);
        lineIdx = lines.indexOf(dimLine) + 1;
        String[] dimensions = dimLine.split(" ");
        int declaredRows = Integer.parseInt(dimensions[0]);
        int declaredCols = Integer.parseInt(dimensions[1]);
        
        String pieceCountLine = getNextNonCommentLine(lines, lineIdx);
        lineIdx = lines.indexOf(pieceCountLine) + 1;

        // Read potential grid lines
        List<String> potentialGridLines = new ArrayList<>();
        for (int i = lineIdx; i < lines.size(); i++) {
            if (!lines.get(i).trim().startsWith("//") && !lines.get(i).trim().isEmpty()) {
                potentialGridLines.add(lines.get(i));
            }
        }
        
        // Find exit positions and actual grid content
        int topExitCol = -1;
        int bottomExitCol = -1;
        boolean leftExitFound = false;
        boolean rightExitFound = false;
        List<String> actualGridLines = new ArrayList<>();
        
        for (int r = 0; r < potentialGridLines.size(); r++) {
            String line = potentialGridLines.get(r);
            String trimmedLine = line.trim();
            
            // Check for standalone 'K' line
            if (trimmedLine.equals("K")) {
                int kCol = line.indexOf('K');
                if (actualGridLines.isEmpty()) {
                    // K is above the grid
                    topExitCol = kCol;
                } else {
                    // K is below the grid
                    bottomExitCol = kCol;
                }
                continue;
            }

            actualGridLines.add(line);
        }
        
        // Create board and configuration array
        Board board = new Board(declaredRows, declaredCols);
        char[][] configuration = new char[declaredRows][declaredCols];
        for (char[] row : configuration) {
            Arrays.fill(row, '.');
        }
        
        // Process grid lines to fill configuration
        for (int r = 0; r < Math.min(actualGridLines.size(), declaredRows); r++) {
            String line = actualGridLines.get(r);
            
            // Check for K at beginning of line
            if (line.startsWith("K")) {
                board.setExit(r, -1);
                leftExitFound = true;
                line = line.substring(1);
            }
            
            // Skip leading spaces
            int gridColIndex = 0;
            for (int c = 0; c < line.length() && gridColIndex < declaredCols; c++) {
                char currentChar = line.charAt(c);
                
                // Skip leading spaces
                if (currentChar == ' ') {
                    continue;
                }
                
                // Handle K within grid (unusual case)
                if (currentChar == 'K') {
                    board.setExit(r, gridColIndex);
                } else {
                    configuration[r][gridColIndex] = currentChar;
                }
                
                gridColIndex++;
            }
            
            // Check for K at end of lin
            if (line.endsWith("K") && !line.trim().equals("K")) {
                board.setExit(r, declaredCols);
                rightExitFound = true;
            }
        }
        
        // Set top/bottom exits if detected
        if (!leftExitFound && !rightExitFound) {
            if (topExitCol != -1) {
                board.setExit(-1, topExitCol);
            } else if (bottomExitCol != -1) {
                board.setExit(declaredRows, bottomExitCol);
            }
        }
        
        
        board.initialize(configuration);
        return board;
    }

    // Read all lines from file.
    private List<String> readAllLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    // Get next non-comment/empty line.
    private String getNextNonCommentLine(List<String> lines, int startIndex) throws IOException {
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.startsWith("//") && !line.isEmpty()) {
                return line;
            }
        }
        throw new IOException("Expected more content in file.");
    }
}