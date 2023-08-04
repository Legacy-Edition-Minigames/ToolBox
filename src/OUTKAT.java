import java.io.*;
import java.net.URL;

public class OUTKAT {
    private static int currentLine = 3; // Start from line 3 as specified in your format
    private static boolean shouldStop = false;

    public static void executeScript(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            String[] scriptLines = new String[0];
            while ((line = reader.readLine()) != null) {
                scriptLines = append(scriptLines, line);
            }
            reader.close();

            String description = scriptLines[0];
            float version = Float.parseFloat(scriptLines[1]);
            String interpreterVersion = scriptLines[2];

            while (currentLine < scriptLines.length && !shouldStop) {
                line = scriptLines[currentLine].trim();
                currentLine++;

                if (!line.isEmpty()) {
                    String[] parts = line.split(" ");
                    String command = parts[0];
                    String[] kArgs = new String[parts.length - 1];
                    System.arraycopy(parts, 1, kArgs, 0, kArgs.length);

                    executeCommand(command, kArgs, scriptLines);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopExecution() {
        shouldStop = true;
    }

    private static void executeCommand(String command, String[] kArgs, String[] scriptLines) {
        switch (command) {
            case "OUT":
                System.out.println(String.join(" ", kArgs));
                break;

            case "DWN":
                downloadFile(kArgs[0], kArgs[1]);
                break;

            case "MOV":
                moveFile(kArgs[0], kArgs[1]);
                break;

            case "GTO":
                int lineToGo = Integer.parseInt(kArgs[0]);
                if (lineToGo >= 0 && lineToGo < scriptLines.length) {
                    currentLine = lineToGo;
                }
                break;

            case "STP":
                stopExecution();
                break;

            // Implement other commands here...

            case "WIT":
                int milliseconds = Integer.parseInt(kArgs[0]);
                try {
                    Thread.sleep(milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            case "KAT":
                // This is a comment line, do nothing.
                break;

            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    private static void downloadFile(String url, String destination) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(destination);
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            fileOutputStream.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void moveFile(String source, String destination) {
        File sourceFile = new File(source);
        File destFile = new File(destination);
        if (sourceFile.renameTo(destFile)) {
            System.out.println("File moved successfully");
        } else {
            System.out.println("Failed to move file");
        }
    }

    // Implement other methods for different commands...

    private static String[] append(String[] array, String item) {
        String[] result = new String[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = item;
        return result;
    }
}
