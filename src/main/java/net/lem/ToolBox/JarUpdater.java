package net.lem.ToolBox;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class JarUpdater {

    private static final String CURRENT_VERSION = "1.5.0";

    public static void update() {
        String jarFilePath = getJarFilePath();
        String downloadUrl = "https://github.com/niceEli/ToolBox/releases/latest/download/toolbox-latest-jar-with-dependencies.jar";

        try {
            String latestVersion = getLatestVersionFromGitHub();

            if (CURRENT_VERSION.equals(latestVersion)) {
            } else {
                if (compareVersion(CURRENT_VERSION, latestVersion) > 0) {
                    System.out.println("CAUTION: The JAR on GitHub is older than the current JAR. This is a test build. Proceed with caution.");
                } else {
                    System.out.println("Updating JAR to version: " + latestVersion);
                    downloadAndReplaceJar(downloadUrl, jarFilePath);
                    System.exit(0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getLatestVersionFromGitHub() throws IOException {
        URL url = new URL("https://api.github.com/repos/niceEli/ToolBox/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.readLine();
            int startIndex = response.indexOf("\"tag_name\":\"") + 12;
            int endIndex = response.indexOf("\"", startIndex);
            return response.substring(startIndex, endIndex);
        }
    }

    private static int compareVersion(String version1, String version2) {
        // Implementation for version comparison (replace this with your comparison logic)
        return version1.compareTo(version2);
    }

    private static String getJarFilePath() {
        String path = JarUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            path = new File(path).getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private static void downloadAndReplaceJar(String downloadUrl, String jarFilePath) throws IOException {
        URL url = new URL(downloadUrl);
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(jarFilePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}