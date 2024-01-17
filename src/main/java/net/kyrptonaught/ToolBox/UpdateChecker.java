package net.kyrptonaught.ToolBox;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyrptonaught.ToolBox.IO.FileHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UpdateChecker {
    public static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    public static String URL = "https://github.com/Legacy-Edition-Minigames/ToolBox/releases";

    public static String isUpdateAvailable() {
        JsonArray response = FileHelper.download(URL.replace("//github.com/", "//api.github.com/repos/"), JsonArray.class);

        if (!response.isEmpty()) {
            String latestRelease = response.get(0).getAsJsonObject().get("tag_name").getAsString();
            if (compareVersions(getVersion(), latestRelease) == -1)
                return latestRelease;
            return null;
        }

        System.out.println("Failed to check for Toolbox Updates");
        return null;
    }

    public static void runAndUpdate() {
        String latestRelease = isUpdateAvailable();
        if (latestRelease != null) {
            installUpdate();
        }

        launchJar(".toolbox/launch.jar", getToolboxRunArgs(), true);
    }

    public static void installUpdate() {
        Menu.clearConsole();
        System.out.println("Installing Toolbox update...");
        try {
            JsonArray response = FileHelper.download(URL.replace("//github.com/", "//api.github.com/repos/"), JsonArray.class);

            if (!response.isEmpty()) {
                JsonArray assets = response.get(0).getAsJsonObject().get("assets").getAsJsonArray();
                for (int i = 0; i < assets.size(); i++) {
                    JsonObject obj = assets.get(i).getAsJsonObject();

                    String[] fileName = obj.get("name").getAsString().split("\\.");

                    String fileExtension = fileName[fileName.length - 1];

                    FileHelper.download(obj.get("browser_download_url").getAsString(), Paths.get(".toolbox").resolve("launch." + fileExtension));

                    FileHelper.writeFile(Paths.get(".toolbox/VERSION"), response.get(0).getAsJsonObject().get("tag_name").getAsString());
                }
            }
        } catch (Exception e) {
            System.out.println("Update failed");
            e.printStackTrace();
            return;
        }

        System.out.println("Update successful");
        System.out.println("Done. Relaunching toolbox...");
        System.out.println();

        Menu.pressEnterToCont(input);
    }

    private static void launchJar(String jar, List<String> args, Boolean waitfor) {
        String[] launchCommands = {"java", "-jar", jar};

        args.addAll(0, List.of(launchCommands));

        try {
            ProcessBuilder launcher = new ProcessBuilder(args)
                    .directory(new File(System.getProperty("user.dir")))
                    .inheritIO();

            Process launchedjar = launcher.start();

            Menu.SKIP_SHUTDOWN_TASKS = true;

            if (waitfor) {
                launchedjar.waitFor();
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static String getVersion() {
        Path versionFile = Paths.get(".toolbox/VERSION");
        if (FileHelper.exists(versionFile)) {
            return FileHelper.readFile(versionFile);
        } else {
            return "0.0";
        }
    }

    public static void runToolbox() {
        launchJar(".toolbox/launch.jar", getToolboxRunArgs(), true);
    }

    public static List<String> getToolboxRunArgs() {
        List<String> arguments = new ArrayList<String>();

        arguments.add("--runToolbox");

        arguments.addAll(List.of(CMDArgsParser.args));

        return arguments;
    }
}
