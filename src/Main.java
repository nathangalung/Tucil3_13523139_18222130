import ui.CLI;
import ui.GUI;

/**
 * Main application entry point
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            // Use command line interface
            CLI.run(args);
        } else {
            // Use graphical interface
            javax.swing.SwingUtilities.invokeLater(() -> {
                new GUI();
            });
        }
    }
}