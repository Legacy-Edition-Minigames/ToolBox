package net.lem.ToolBox;

import net.lem.ToolBox.GUIIcons.Floppa;
import net.lem.ToolBox.GUIIcons.LEMLogo;

import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException {
        JarUpdater.update();
        Conf.createIfNotExists();
        LEMLogo.GUI();

        if (args.length == 0) {
            noGUI.GUI();
        } else if (args[0].equals("--inject")) {
            OUTKAT.executeScript(args[1]);
        } else if (args[0].equals("--injectCRC")) {
            FileWorkerThreads.RUNCRC(args[1]);
        } else if (args[0].equals("gui")) {
            mainMenu.GUI();
        } else if (args[0].equals("floppa")) {
            Floppa.GUI();
        } else if (args[0].equals("config")) {
            Conf.set(args[1], args[2]);
            System.out.println("Line updated");
        } else if (args[0].equals("gConfig")) {
            System.out.println(Conf.get(Integer.parseInt(args[1])));
        } else {
            System.out.println("Available Args:");
            System.out.println("empty");
            System.out.println("--inject (OUTKAT file)");
            System.out.println("--injectCRC (CRC File)");
            System.out.println("gui");
            System.out.println("config (Key) (Value)");
            System.out.println("gConfig (key)");
        }
    }
}