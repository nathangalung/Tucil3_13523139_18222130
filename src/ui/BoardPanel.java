package ui;

import core.Board;
import core.Piece;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JPanel;

/**
 * Renders the game board.
 */
public class BoardPanel extends JPanel {
    private Board board;
    private final int cellSize = 60;
    private final Map<Character, Color> pieceColors;
    private final int margin = cellSize;
    private final Random random = new Random();
    
    public BoardPanel(Board board) {
        this.board = board;
        this.pieceColors = new HashMap<>();
        pieceColors.put('P', Color.RED);
        
        assignPieceColors();
        updatePreferredSizeAndRevalidate();
    }

    // Assigns colors to pieces.
    private void assignPieceColors() {
        if (this.board != null && this.board.getPieces() != null) {
            for (Piece piece : this.board.getPieces()) {
                char id = piece.getId();
                if (id != 'P' && !pieceColors.containsKey(id)) {
                    pieceColors.put(id, generateRandomColor());
                }
            }
        }
    }

    // Updates preferred size and revalidates.
    private void updatePreferredSizeAndRevalidate() {
        int cols = (this.board != null && this.board.getCols() > 0) ? this.board.getCols() : 6;
        int rows = (this.board != null && this.board.getRows() > 0) ? this.board.getRows() : 6;
        
        int panelWidth = cols * this.cellSize + 2 * this.margin;
        int panelHeight = rows * this.cellSize + 2 * this.margin;
        
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        revalidate();
    }
    
    // Get current board.
    public Board getBoard() {
        return board;
    }
    
    // Update displayed board.
    public void updateBoard(Board newBoard) {
        this.board = newBoard;
        assignPieceColors();
        updatePreferredSizeAndRevalidate();
        repaint();
    }
    
    // Generate random piece color.
    private Color generateRandomColor() {
        return new Color(random.nextInt(180), random.nextInt(180), random.nextInt(180));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(margin, margin); 
        
        int rows = board.getRows();
        int cols = board.getCols();
        
        // Draw grid cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board.isRowSpaceOnly(r) || board.isColSpaceOnly(c)) {
                    g2d.setColor(getBackground()); 
                    g2d.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawRect(c * cellSize, r * cellSize, cellSize, cellSize);
                }
            }
        }
        
        // Draw pieces
        g2d.setFont(new Font("Arial", Font.BOLD, cellSize / 3));
        FontMetrics fm = g2d.getFontMetrics();

        for (Piece piece : board.getPieces()) {
            int r = piece.getRow();
            int c = piece.getCol();
 
            if (r < -piece.getSize() || r >= rows + piece.getSize() || 
                c < -piece.getSize() || c >= cols + piece.getSize()){
                continue;
            }

            g2d.setColor(pieceColors.getOrDefault(piece.getId(), Color.DARK_GRAY));
            int pieceWidth = piece.isHorizontal() ? piece.getSize() * cellSize : cellSize;
            int pieceHeight = piece.isHorizontal() ? cellSize : piece.getSize() * cellSize;
            g2d.fillRect(c * cellSize + 2, r * cellSize + 2, pieceWidth - 4, pieceHeight - 4);
            
            g2d.setColor(Color.WHITE);
            String label = String.valueOf(piece.getId());
            int textX = c * cellSize + (pieceWidth - fm.stringWidth(label)) / 2;
            int textY = r * cellSize + (pieceHeight - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(label, textX, textY);
        }
        
        // Draw exit 'K'
        drawExit(g2d, rows, cols, fm);
        g2d.dispose();
    }

    // Helper to draw exit.
    private void drawExit(Graphics2D g2d, int rows, int cols, FontMetrics fm) {
        int exitR = board.getExitRow();
        int exitC = board.getExitCol();
        if (exitR == -1 && exitC == -1 && (board.getPrimaryPiece() == null || !board.getPrimaryPiece().isPrimary())) return;


        g2d.setColor(Color.GREEN);
        String kLabel = "K";
        int textWidth = fm.stringWidth(kLabel);
        int textHeight = fm.getAscent();
        int kRectX = 0, kRectY = 0, kRectW = cellSize, kRectH = cellSize;

        // Determine K position
        if (exitC == cols) { kRectX = cols * cellSize; kRectY = exitR * cellSize; } // Right
        else if (exitC == -1) { kRectX = -cellSize; kRectY = exitR * cellSize; }    // Left
        else if (exitR == rows) { kRectX = exitC * cellSize; kRectY = rows * cellSize; } // Bottom
        else if (exitR == -1) { kRectX = exitC * cellSize; kRectY = -cellSize; }    // Top
        else if (exitR >= 0 && exitR < rows && exitC >= 0 && exitC < cols) { // Within grid
            kRectX = exitC * cellSize; kRectY = exitR * cellSize;
        } else return;

        g2d.fillRect(kRectX + 2, kRectY + 2, kRectW - 4, kRectH - 4);
        g2d.setColor(Color.BLACK);
        g2d.drawString(kLabel, 
                       kRectX + (kRectW - textWidth) / 2, 
                       kRectY + (kRectH - fm.getHeight()) / 2 + textHeight);
    }
}