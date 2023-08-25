package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static void main(String[] args) {
        GUI();
        Path toolboxTempPath = Path.of(".toolbox");

        BranchesConfig branches = ConfigLoader.parseBranches(FileHelper.download("file:test/TestBranches.json"));

        branches.branches.forEach((s, s2) -> System.out.println(s + " " + s2));

        Path basePath = Path.of("server");
        FileHelper.createDir(basePath.resolve(".toolbox"));
        FileHelper.createDir(toolboxTempPath.resolve("downloads"));

        BranchConfig branch = ConfigLoader.parseToolboxConfig(FileHelper.download("file:test/main/mainBranch.json"));

        System.out.println("Downloading dependencies...");
        for (BranchConfig.Dependency dependency : branch.dependencies) {
            Path tempLocation = toolboxTempPath.resolve("downloads/" + dependency.name);
            if (dependency.location.startsWith("/"))
                dependency.location = dependency.location.substring(1);
            Path destination = basePath.resolve(dependency.location);

            System.out.print("Downloading " + dependency.name + "...");
            FileHelper.download(dependency.url, tempLocation);
            String hash = FileHelper.hashFile(tempLocation);

            System.out.println("done");

            FileHelper.createDir(destination);
            if (dependency.unzip) {
                FileHelper.unzipFile(tempLocation, destination);
                FileHelper.writeHash(basePath.resolve(".toolbox").resolve(dependency.name + ".hash"), hash);
            } else {
                FileHelper.moveFile(tempLocation, destination.resolve(dependency.name));
            }
        }

        System.out.println("Dependencies done");
        System.out.println("Starting server: " + branch.launchCMD);

        AtomicReference<Process> process = new AtomicReference<>();
        new Thread(() -> {
            try {
                process.set(new ProcessBuilder(branch.launchCMD.split(" "))
                        .directory(new File(System.getProperty("user.dir") + "/server/"))
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
}