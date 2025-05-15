package core;

import java.io.*;

/**
 * Parse puzzle input files
 */
public class FileParser {
    private String filePath;
    
    public FileParser(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Parse puzzle file
     */
    public Board parseFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        
        String line;
        do {
            line = reader.readLine();
        } while (line != null && line.trim().startsWith("//"));
        
        // Read dimensions
        String[] dimensions = line.split(" ");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);
        
        // Skip the number of pieces line
        reader.readLine();
        
        // Read the board configuration
        char[][] configuration = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            line = reader.readLine();
            for (int j = 0; j < Math.min(cols, line.length()); j++) {
                configuration[i][j] = line.charAt(j);
            }
        }
        
        reader.close();
        
        // Create and initialize the board
        Board board = new Board(rows, cols);
        board.initialize(configuration);
        
        return board;
    }
}