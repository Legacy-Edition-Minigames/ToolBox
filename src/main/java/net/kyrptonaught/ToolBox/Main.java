package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.IO.FileHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class Main {
    //Γûä
    public static void main(String[] args) {
        if (args.length > 1 && args[0].equals("--runToolbox") && args[1].equals("--autoHash")) {
            AutoHash.autoHash();
        } else if (args.length > 0 && args[0].equals("--runToolbox")) {
            Menu.startStateMachine(args);
        } else {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            CMDArgsParser.setArgs(args);
            Menu.checkForUpdate(input);
        }
    }
}