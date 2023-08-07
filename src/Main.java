import GUIIcons.Floppa;
import GUIIcons.LEMLogo;

import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Conf conf = new Conf();
        try {
            conf.createIfNotExists();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LEMLogo.GUI();

        if(args.length == 0){
            System.out.println("dont worry its not broken your just accessing something that isn't done yet, you can use args such as --inject to run an OUTKAT file or gui to try and run the gui");
        } else if (args[0].equals("--inject")) {
            OUTKAT.executeScript(args[1]);
        } else if (args[0].equals("gui")) {
            mainMenu.GUI();

        } else if (args[0].equals("floppa")) {
            Floppa.GUI();
        } else {
            System.out.println("Available Args:");
            System.out.println("empty");
            System.out.println("--inject (outkat file)");
            System.out.println("gui");
        }
    }
}