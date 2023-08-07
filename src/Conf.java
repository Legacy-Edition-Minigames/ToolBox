import java.io.*;

public class Conf {

    public String get(int[] args){
        try {
            createIfNotExists(); // Call the create method if the file doesn't exist

            BufferedReader reader = new BufferedReader(new FileReader("./LEMToolbox.cfg"));
            String line;
            String[] scriptLines = new String[0];
            while ((line = reader.readLine()) != null) {
                scriptLines = append(scriptLines, line);
            }
            reader.close();

            return scriptLines[args[0]];
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public void set(String[] args){
        try {
            createIfNotExists(); // Call the create method if the file doesn't exist

            File file = new File("./LEMToolbox.cfg");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            String[] scriptLines = new String[0];
            while ((line = reader.readLine()) != null) {
                scriptLines = append(scriptLines, line);
            }
            reader.close();

            int lineIndex = Integer.parseInt(args[0]);
            if (lineIndex >= 0 && lineIndex < scriptLines.length) {
                scriptLines[lineIndex] = args[1];
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

    public void createIfNotExists() throws IOException {
        File file = new File("./LEMToolbox.cfg");
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private static String[] append(String[] array, String item) {
        String[] result = new String[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = item;
        return result;
    }
}
