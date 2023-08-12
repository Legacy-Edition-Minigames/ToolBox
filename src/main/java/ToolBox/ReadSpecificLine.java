package ToolBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadSpecificLine {
    public static String main(String[] args) {
        if (args.length != 2) {
            return null;
        }

        String filePath = args[0];
        int lineNumber = Integer.parseInt(args[1]);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int currentLine = 1;

            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    return line;
                }
                currentLine++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
