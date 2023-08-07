import java.io.*;
import java.util.ArrayList;

public class Conf {

    public static String get(int args){
        try {
            createIfNotExists(); // Call the create method if the file doesn't exist

            BufferedReader reader = new BufferedReader(new FileReader("./LEMToolbox.cfg"));
            String line;
            String[] scriptLines = new String[0];
            while ((line = reader.readLine()) != null) {
                scriptLines = append(scriptLines, line);
            }
            reader.close();

            return scriptLines[args];
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void set(String key, String value) {
        try {
            createIfNotExists(); // Call the create method if the file doesn't exist

            File file = new File("./LEMToolbox.cfg");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArrayList<String> scriptLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                scriptLines.add(line);
            }
            reader.close();

            int lineIndex = Integer.parseInt(key);
            if (lineIndex >= scriptLines.size()) {
                // Add new lines up to the specified index
                while (scriptLines.size() <= lineIndex) {
                    scriptLines.add("");
                }
            }

            if (lineIndex >= 0 && lineIndex < scriptLines.size()) {
                scriptLines.set(lineIndex, value);
            } else {
                System.out.println("Invalid line index.");
                return;
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String updatedLine : scriptLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            writer.close();

            System.out.println("Line updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createIfNotExists() throws IOException {
        File file = new File("./LEMToolbox.cfg");
        if (!file.exists()) {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("main");
            writer.newLine();
            writer.write("0.0");
            writer.newLine();
            writer.write("niceEli.github.io/ToolBox/Branches"); // Change To Emmie's Site After Merge
            writer.newLine();
            writer.close();
        }
    }

    private static String[] append(String[] array, String item) {
        String[] result = new String[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = item;
        return result;
    }
}
