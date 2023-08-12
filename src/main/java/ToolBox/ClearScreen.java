package ToolBox;

import java.io.IOException;

public class ClearScreen {
    public static void Execute() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
