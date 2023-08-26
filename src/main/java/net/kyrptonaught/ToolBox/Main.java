package net.kyrptonaught.ToolBox;

import com.google.gson.JsonObject;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static void main(String[] args) {
        GUI();
        Path toolboxTempPath = Path.of(".toolbox");

        BranchesConfig branches = ConfigLoader.parseBranches(FileHelper.download("https://raw.githubusercontent.com/Legacy-Edition-Minigames/ToolBox/java/testConfigs/TestBranches.json"));

        branches.branches.forEach((s, s2) -> System.out.println(s + " " + s2));

        BranchConfig branch = ConfigLoader.parseToolboxConfig(FileHelper.download("https://raw.githubusercontent.com/Legacy-Edition-Minigames/ToolBox/java/testConfigs/main/mainBranch.json"));

        Path basePath = Path.of("installs/" + branch.name);

        FileHelper.createDir(basePath.resolve(".toolbox"));
        FileHelper.createDir(toolboxTempPath.resolve("downloads"));


        System.out.println("Downloading dependencies...");
        for (BranchConfig.Dependency dependency : branch.dependencies) {
            Path tempLocation = toolboxTempPath.resolve("downloads/" + dependency.name);
            if (dependency.location.startsWith("/"))
                dependency.location = dependency.location.substring(1);

            System.out.print("Checking " + dependency.name + "...");

            String hash = getNewHash(dependency, tempLocation);
            String existingHash = hashExistingFile(dependency, basePath);

            if (hash != null && !hash.equals(existingHash)) {
                installFile(dependency, hash, tempLocation, basePath);
                System.out.println("Installed");
            } else {
                System.out.println("Already exists");
            }
        }

        FileHelper.deleteDirectory(toolboxTempPath);
        System.out.println("Dependencies done");
        System.out.println("Starting server: " + branch.launchCMD);


        AtomicReference<Process> process = new AtomicReference<>();
        new Thread(() -> {
            try {
                process.set(new ProcessBuilder(branch.launchCMD.split(" "))
                        .directory(new File(System.getProperty("user.dir") + "/" + basePath + "/"))
                        .redirectErrorStream(true)
                        .start());
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.get().getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Server: " + line);
                }

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        while (process.get() == null) {
            //we have to wait for the process to start
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.get().getOutputStream()))) {
            int line;
            while ((line = br.read()) != -1) {
                writer.write(line);
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void GUI() {
        System.out.println(" ▄▄▄     ▄▄▄▄▄▄▄ ▄▄   ▄▄    ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄▄     ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄   ▄▄ ");
        System.out.println("█   █   █       █  █▄█  █  █       █       █       █   █   █  ▄    █       █  █▄█  █");
        System.out.println("█   █   █    ▄▄▄█       █  █▄     ▄█   ▄   █   ▄   █   █   █ █▄█   █   ▄   █       █");
        System.out.println("█   █   █   █▄▄▄█       █    █   █ █  █ █  █  █ █  █   █   █       █  █ █  █       █");
        System.out.println("█   █▄▄▄█    ▄▄▄█       █    █   █ █  █▄█  █  █▄█  █   █▄▄▄█  ▄   ██  █▄█  ██     █ ");
        System.out.println("█       █   █▄▄▄█ ██▄██ █    █   █ █       █       █       █ █▄█   █       █   ▄   █");
        System.out.println("█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄█   █▄█    █▄▄▄█ █▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄█ █▄▄█");
        System.out.println();
    }

    public static String convertRepoToZipball(String repo) {
        return repo.replace("/tree/", "/archive/refs/heads/") + ".zip";
    }

    public static String getNewHash(BranchConfig.Dependency dependency, Path tempLocation) {
        if (dependency.gitRepo) {
            String repo = dependency.url;
            dependency.url = convertRepoToZipball(repo);
            JsonObject response = FileHelper.download("https://api.github.com/repos/Legacy-Edition-Minigames/Minigames/branches/toolbox-testing", JsonObject.class);
            return response.getAsJsonObject("commit").getAsJsonPrimitive("sha").getAsString();
        } else {
            FileHelper.download(dependency.url, tempLocation);
            return FileHelper.hashFile(tempLocation);
        }
    }

    public static String hashExistingFile(BranchConfig.Dependency dependency, Path basePath) {
        if (dependency.unzip) {
            Path hashFile = basePath.resolve(".toolbox").resolve(dependency.name + ".hash");
            if (Files.exists(hashFile) && Files.isReadable(hashFile))
                return FileHelper.readHash(hashFile);
        } else {
            Path hashFile = basePath.resolve(dependency.location).resolve(dependency.name);
            if (Files.exists(hashFile) && Files.isReadable(hashFile))
                return FileHelper.hashFile(hashFile);
        }
        return null;
    }

    public static void installFile(BranchConfig.Dependency dependency, String hash, Path tempLocation, Path basePath) {
        Path destination = basePath.resolve(dependency.location);
        FileHelper.createDir(destination);

        if (dependency.gitRepo) {
            System.out.print("downloading...");
            FileHelper.download(dependency.url, tempLocation);
        }

        if (dependency.unzip) {
            FileHelper.unzipFile(tempLocation, destination);
            FileHelper.writeHash(basePath.resolve(".toolbox").resolve(dependency.name + ".hash"), hash);
        } else {
            FileHelper.moveFile(tempLocation, destination.resolve(dependency.name));
        }
    }
}