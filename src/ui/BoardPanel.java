package ui;

import core.Board;
import core.Piece;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * GUI board rendering panel
 */
public class BoardPanel extends JPanel {
    private Board board;
    private int cellSize;
    private Map<Character, Color> pieceColors;
    
    public BoardPanel(Board board) {
        this.board = board;
        this.cellSize = 60;
        
        // Initialize piece colors
        pieceColors = new HashMap<>();
        pieceColors.put('P', Color.RED);
        
        // Generate random colors for other pieces
        for (Piece piece : board.getPieces()) {
            char id = piece.getId();
            if (id != 'P' && !pieceColors.containsKey(id)) {
                pieceColors.put(id, generateRandomColor());
            }
        }
   
        setPreferredSize(new Dimension(
            (board.getCols() + 1) * cellSize,
            (board.getRows() + 1) * cellSize
        ));
    }
    
    /**
     * Get board reference
     */
    public Board getBoard() {
        return board;
    }
    
    /**
     * Update board state
     */
    public void updateBoard(Board board) {
        this.board = board;
        repaint();
    }
    
    /**
     * Generate random piece color
     */
    private Color generateRandomColor() {
        // Avoid colors that are too light
        return new Color(
            (int)(Math.random() * 200),
            (int)(Math.random() * 200),
            (int)(Math.random() * 200)
        );
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int rows = board.getRows();
        int cols = board.getCols();
        
        // Draw grid for the board
        g2d.setColor(Color.LIGHT_GRAY);
        for (int row = 0; row <= rows; row++) {
            g2d.drawLine(0, row * cellSize, cols * cellSize, row * cellSize);
        }
        for (int col = 0; col <= cols; col++) {
            g2d.drawLine(col * cellSize, 0, col * cellSize, rows * cellSize);
        }
        
        // Draw pieces
        for (Piece piece : board.getPieces()) {
            int row = piece.getRow();
            int col = piece.getCol();
            int size = piece.getSize();
            boolean isHorizontal = piece.isHorizontal();
            char id = piece.getId();
            
            // Set piece color
            g2d.setColor(pieceColors.getOrDefault(id, Color.GRAY));
            
            // Draw the piece
            if (isHorizontal) {
                g2d.fillRect(
                    col * cellSize + 2,
                    row * cellSize + 2,
                    size * cellSize - 4,
                    cellSize - 4
                );
            } else {
                g2d.fillRect(
                    col * cellSize + 2,
                    row * cellSize + 2,
                    cellSize - 4,
                    size * cellSize - 4
                );
            }
            
            // Draw piece label
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g2d.getFontMetrics();
            String label = String.valueOf(id);
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getHeight();
            
            if (isHorizontal) {
                g2d.drawString(
                    label,
                    col * cellSize + (cellSize * size / 2) - textWidth / 2,
                    row * cellSize + cellSize / 2 + textHeight / 3
                );
            } else {
                g2d.drawString(
                    label,
                    col * cellSize + cellSize / 2 - textWidth / 2,
                    row * cellSize + (cellSize * size / 2) + textHeight / 3
                );
            }
        }
        
        // Draw exit
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        if (exitRow >= 0 && exitCol >= 0) {
            g2d.setColor(Color.GREEN);
            int margin = 5;
            g2d.fillRect(
                exitCol * cellSize + margin,
                exitRow * cellSize + margin,
                cellSize - 2*margin,
                cellSize - 2*margin
            );
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("K", exitCol * cellSize + cellSize / 2 - 7, exitRow * cellSize + cellSize / 2 + 7);
        }
    }
}