package net.kyrptonaught.ToolBox;

import com.google.gson.JsonObject;
import net.kyrptonaught.ToolBox.IO.ConfigLoader;
import net.kyrptonaught.ToolBox.IO.FileHelper;
import net.kyrptonaught.ToolBox.IO.GithubHelper;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.holders.InstalledServerInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Installer {

    public static List<InstalledServerInfo> detectInstalls() {
        Path installPath = Path.of("installs");

        List<InstalledServerInfo> configs = new ArrayList<>();
        try (Stream<Path> files = Files.walk(installPath, 1)) {
            files.forEach(path -> {
                if (Files.isDirectory(path) && Files.exists(path.resolve(".toolbox").resolve("meta").resolve("toolbox.json"))) {
                    InstalledServerInfo serverInfo = ConfigLoader.parseToolboxInstall(FileHelper.readFile(path.resolve(".toolbox").resolve("meta").resolve("toolbox.json")));
                    serverInfo.setPath(path);
                    configs.add(serverInfo);
                }
            });
        } catch (IOException ignored) {
        }

        return configs;
    }

    public static void installAndCheckForUpdates(InstalledServerInfo serverInfo) {
        FileHelper.createDir(serverInfo.getPath());

        FileHelper.createDir(serverInfo.getMetaPath());
        FileHelper.createDir(serverInfo.getDownloadPath());
        FileHelper.createDir(serverInfo.getHashPath());
        FileHelper.createDir(serverInfo.getLogPath());

        System.out.println("Checking dependencies...");
        installDependencies(serverInfo);

        FileHelper.writeFile(serverInfo.getMetaPath().resolve("toolbox.json"), ConfigLoader.serializeToolboxInstall(serverInfo));
        System.out.println("Dependencies done");
    }

    public static void verifyInstall(InstalledServerInfo serverInfo) {
        for (BranchConfig.Dependency dependency : serverInfo.getDependencies()) {
            System.out.println("Checking files from dependency: " + dependency.getDisplayName());
            List<String> installedFiles = FileHelper.readLines(serverInfo.getLogPath(dependency));
            if (installedFiles != null)
                for (String file : installedFiles)
                    if (!FileHelper.exists(Path.of(file))) {
                        replaceMissingFiles(serverInfo, dependency);
                        break;
                    }
        }
    }

    public static void packageInstall(InstalledServerInfo serverInfo) {
        FileHelper.zipDirectory(serverInfo.getPath(), Path.of("packaged/" + serverInfo.getName() + ".toolbox"));
    }

    private static void installDependencies(InstalledServerInfo serverInfo) {
        for (BranchConfig.Dependency dependency : serverInfo.getDependencies()) {

            if (dependency.location.startsWith("/"))
                dependency.location = dependency.location.substring(1);

            System.out.print("Checking " + dependency.getDisplayName() + "...");

            String hash = getNewHash(serverInfo, dependency);
            String existingHash = hashExistingFile(serverInfo, dependency);

            if (hash != null && !hash.equals(existingHash)) {
                System.out.print("downloading...");
                installFile(serverInfo, dependency, hash);
                System.out.println("installed");
            } else {
                System.out.println("Already exists");
            }
        }
        detectRemovedDependencies(serverInfo);
    }

    private static String getNewHash(InstalledServerInfo serverInfo, BranchConfig.Dependency dependency) {
        if (dependency.gitRepo) {
            String apiCall = GithubHelper.convertRepoToApiCall(dependency.url);
            JsonObject response = FileHelper.download(apiCall, JsonObject.class);
            return response.getAsJsonObject("commit").getAsJsonPrimitive("sha").getAsString();
        } else {
            Path downloadPath = serverInfo.getDownloadPath(dependency);
            FileHelper.download(dependency.url, downloadPath);
            return FileHelper.hashFile(downloadPath);
        }
    }

    private static String hashExistingFile(InstalledServerInfo serverInfo, BranchConfig.Dependency dependency) {
        Path hashFile = serverInfo.getHashPath(dependency);
        if (Files.exists(hashFile) && Files.isReadable(hashFile))
            return FileHelper.readFile(hashFile);
        return null;
    }

    private static void installFile(InstalledServerInfo serverInfo, BranchConfig.Dependency dependency, String hash) {
        Path downloadPath = serverInfo.getDownloadPath(dependency);
        Path destination = serverInfo.getDependencyPath(dependency);
        FileHelper.createDir(destination);

        //checking hash already downloaded other file types
        if (dependency.gitRepo) {
            FileHelper.download(GithubHelper.convertRepoToZipball(dependency.url), downloadPath);
        }

        clearOldFiles(serverInfo.getLogPath(dependency));

        List<String> installedFiles;
        if (dependency.unzip) {
            installedFiles = FileHelper.unzipFile(downloadPath, destination);
        } else {
            installedFiles = FileHelper.copyFile(downloadPath, destination.resolve(dependency.name));
        }

        installedFiles.add(destination.toString());
        FileHelper.writeFile(serverInfo.getHashPath(dependency), hash);
        FileHelper.writeLines(serverInfo.getLogPath(dependency), installedFiles);
    }

    private static void replaceMissingFiles(InstalledServerInfo serverInfo, BranchConfig.Dependency dependency) {
        Path downloadPath = serverInfo.getDownloadPath(dependency);
        Path destination = serverInfo.getDependencyPath(dependency);
        FileHelper.createDir(destination);

        if (dependency.unzip) {
            List<String> installedFiles = FileHelper.readLines(serverInfo.getLogPath(dependency));
            if (installedFiles != null) {
                Path unzipPath = serverInfo.getTempLocation(dependency);
                FileHelper.unzipFile(downloadPath, unzipPath);
                installedFiles.sort(Comparator.naturalOrder());
                for (String file : installedFiles) {
                    if (!FileHelper.exists(Path.of(file))) {
                        System.out.println("Replacing file: " + file);

                        Path path = Path.of(file);
                        if (Files.isDirectory(path)) {
                            FileHelper.createDir(path);
                        } else {
                            FileHelper.copyFile(Path.of(file.replace(serverInfo.getPath().toString(), unzipPath.toString())), path);
                        }
                    }
                }
                FileHelper.deleteDirectory(serverInfo.getTempLocation());
            }
        } else {
            System.out.println("Replacing file: " + destination.resolve(dependency.name));
            FileHelper.copyFile(downloadPath, destination.resolve(dependency.name));
        }
    }

    private static void detectRemovedDependencies(InstalledServerInfo serverInfo) {
        try (Stream<Path> logFiles = Files.list(serverInfo.getLogPath())) {
            for (Path logPath : logFiles.toList()) {
                String depName = logPath.getFileName().toString().replace(".installed", "");
                boolean found = false;
                for (BranchConfig.Dependency dependency : serverInfo.getDependencies()) {
                    if (dependency.name.equals(depName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Removing deleted dependency: " + depName);
                    clearOldFiles(logPath);
                    FileHelper.delete(serverInfo.getDownloadPath().resolve(Path.of(depName)));
                    FileHelper.delete(serverInfo.getHashPath().resolve(Path.of(depName + ".hash")));
                    FileHelper.delete(logPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearOldFiles(Path logPath) {
        if (Files.exists(logPath)) {
            List<String> previousInstalledFiles = FileHelper.readLines(logPath);
            if (previousInstalledFiles != null) {
                for (String string : previousInstalledFiles) {
                    FileHelper.delete(Path.of(string));
                }
            }
        }
    }
}