package net.kyrptonaught.ToolBox;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyrptonaught.ToolBox.IO.FileHelper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class UpdateChecker {

    //todo change on every new release
    public static String version = "1.0";

    public static String URL = "https://github.com/Legacy-Edition-Minigames/ToolBox/releases";

    public static String isUpdateAvailable() {
        JsonArray response = FileHelper.download(URL.replace("//github.com/", "//api.github.com/repos/"), JsonArray.class);

        if (!response.isEmpty()) {
            String latestRelease = response.get(0).getAsJsonObject().get("tag_name").getAsString();
            if (compareVersions(version, latestRelease) == -1)
                return latestRelease;
            return null;
        }

        System.out.println("Failed to check for Toolbox Updates");
        return null;
    }

    public static void prepUpdate() {
        FileHelper.createDir(Paths.get(".toolbox").resolve("update"));

        JsonArray response = FileHelper.download(URL.replace("//github.com/", "//api.github.com/repos/"), JsonArray.class);

        if (!response.isEmpty()) {
            JsonArray assets = response.get(0).getAsJsonObject().get("assets").getAsJsonArray();
            for (int i = 0; i < assets.size(); i++) {
                JsonObject obj = assets.get(i).getAsJsonObject();
                FileHelper.download(obj.get("browser_download_url").getAsString(), Paths.get(".toolbox").resolve("update").resolve(obj.get("name").getAsString()));
            }

            FileHelper.copyFile(Paths.get(".").resolve("ToolBox2.0.jar"), Paths.get(".toolbox").resolve("Updater.jar"));
            FileHelper.writeFile(Paths.get(".toolbox").resolve("UPDATE_IN_PROGRESS"), "rua");

            try {
                new ProcessBuilder("java", "-jar", ".toolbox/Updater.jar", "--updater")
                        .directory(new File(System.getProperty("user.dir")))
                        .start();

                Menu.SKIP_SHUTDOWN_TASKS = true;
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void installUpdate() {
        try (Stream<Path> files = Files.walk(Paths.get(".toolbox").resolve("update"), 1)) {
            Thread.sleep(5000);

            files.forEach(path -> {
                if (!Files.isDirectory(path)) {
                    FileHelper.copyFile(path, path.getFileName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        FileHelper.deleteDirectory(Paths.get(".toolbox").resolve("update"));
        FileHelper.delete(Paths.get(".toolbox").resolve("UPDATE_IN_PROGRESS"));
    }

    private static int compareVersions(String version1, String version2) {
        int comparisonResult = 0;

        String[] version1Splits = version1.split("\\.");
        String[] version2Splits = version2.split("\\.");
        int maxLengthOfVersionSplits = Math.max(version1Splits.length, version2Splits.length);

        for (int i = 0; i < maxLengthOfVersionSplits; i++) {
            Integer v1 = i < version1Splits.length ? Integer.parseInt(version1Splits[i]) : 0;
            Integer v2 = i < version2Splits.length ? Integer.parseInt(version2Splits[i]) : 0;
            int compare = v1.compareTo(v2);
            if (compare != 0) {
                comparisonResult = compare;
                break;
            }
        }
        return comparisonResult;
    }
}
