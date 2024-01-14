package net.kyrptonaught.ToolBox;

public class Main {
    //Γûä
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--autoHash")) {
            AutoHash.autoHash();
            return;
        } else if (args.length > 0 && args[0].equals("--updater")) {
            UpdateChecker.installUpdate();
            return;
        }

        Menu.startStateMachine(args);
    }
}