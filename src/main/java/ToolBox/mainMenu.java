package ToolBox;

import ToolBox.GUIIcons.LEMLogo;

import java.io.IOError;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class mainMenu {
    public static void GUI() throws IOException {
        ClearScreen.Execute();
        LEMLogo.GUI();
        JarUpdater.update();
        System.out.println("Created By niceEli, DBTDerpblox, and YOU!");
        System.out.println();
        System.out.println("What Would You Like To Do");
        System.out.println("0: Run Server");
        System.out.println("1: Exit");
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        int OBJ = scanner.nextInt();
        scanner.close();

        if (Objects.equals(OBJ, 0)) {
            noGUI.GUI();
        } else if (Objects.equals(OBJ, 1)) {
            System.exit(0);
        } else {
            mainMenu.GUI();
        }

    }
}
