import javax.swing.SwingUtilities;
import ui.CLI;
import ui.GUI;

/**
 * Main application entry point.
 * Launches CLI or GUI.
 */
public class Main {
    public static void main(String[] args) {
        boolean useCLI = false;
        if (args != null) {
            for (String arg : args) {
                if ("--cli".equalsIgnoreCase(arg) || "-cli".equalsIgnoreCase(arg)) {
                    useCLI = true;
                    break;
                }
            }
        }

        if (useCLI) {
            CLI.run(); 
        } else {
            SwingUtilities.invokeLater(GUI::new); 
        }
    }
}