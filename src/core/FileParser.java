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
        int fileRows = Integer.parseInt(dimensions[0]);
        int fileCols = Integer.parseInt(dimensions[1]);
        
        String pieceCountLine = getNextNonCommentLine(lines, lineIdx);
        lineIdx = lines.indexOf(pieceCountLine) + 1;

        Board board = new Board(fileRows, fileCols);
        char[][] configuration = new char[fileRows][fileCols];
        for (char[] rowConfig : configuration) {
            Arrays.fill(rowConfig, ' ');
        }

        List<String> potentialGridLines = new ArrayList<>();
        for (int i = lineIdx; i < lines.size(); i++) {
            String currentLine = lines.get(i).trim();
            if (!currentLine.startsWith("//") && !currentLine.isEmpty()) {
                potentialGridLines.add(lines.get(i)); 
            }
        }
        
        boolean exitSetExplicitly = false;
        List<String> actualGridLines = new ArrayList<>();

        for (String currentLine : potentialGridLines) {
            String trimmedLine = currentLine.trim();
            if (trimmedLine.equals("K") && !exitSetExplicitly) {
                int kCol = currentLine.indexOf('K');
                if (actualGridLines.isEmpty()) { 
                    board.setExit(-1, kCol);
                    exitSetExplicitly = true;
                } else if (actualGridLines.size() >= fileRows) { 
                    board.setExit(fileRows, kCol);
                    exitSetExplicitly = true;
                } else {
                    if (actualGridLines.size() < fileRows) actualGridLines.add(currentLine);
                }
            } else if (actualGridLines.size() < fileRows) {
                actualGridLines.add(currentLine);
            }
        }
        
        for (int r = 0; r < fileRows; r++) {
            if (r >= actualGridLines.size()) continue; 

            String lineContent = actualGridLines.get(r);
            String effectiveContent = lineContent;

            if (!exitSetExplicitly && lineContent.length() > 0 && lineContent.charAt(0) == 'K') {
                board.setExit(r, -1);
                exitSetExplicitly = true;
                effectiveContent = lineContent.substring(1);
            }

            for (int c = 0; c < fileCols; c++) {
                if (c < effectiveContent.length()) {
                    char charVal = effectiveContent.charAt(c);
                    if (charVal == 'K' && !exitSetExplicitly) {
                        board.setExit(r, c);
                        exitSetExplicitly = true;
                        configuration[r][c] = ' ';
                    } else {
                        configuration[r][c] = charVal;
                    }
                }
            }

            if (!exitSetExplicitly && lineContent.length() == fileCols + 1 && lineContent.charAt(fileCols) == 'K') {
                board.setExit(r, fileCols); 
                exitSetExplicitly = true;
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