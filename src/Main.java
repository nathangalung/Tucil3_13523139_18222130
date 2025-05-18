import ui.CLI;
import ui.GUI;

public class Main {
    public static void main(String[] args) {
        boolean useCLI = false;
        for (String arg : args) {
            if ("--cli".equals(arg) || "-cli".equals(arg)) {
                useCLI = true;
                break;
            }
        }
        if (useCLI) {
            CLI.run(args);
        } else {
            GUI.main(args);
        }
    }
}