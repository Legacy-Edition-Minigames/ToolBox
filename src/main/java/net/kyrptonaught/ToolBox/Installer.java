package net.kyrptonaught.ToolBox;

import com.google.gson.JsonObject;
import net.kyrptonaught.ToolBox.configs.BranchConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Installer {

    public static void installAndCheckForUpdates(InstalledServerInfo serverInfo) {
        FileHelper.createDir(serverInfo.getPath());

        FileHelper.createDir(serverInfo.getMetaPath());
        FileHelper.createDir(serverInfo.getDownloadPath());
        FileHelper.createDir(serverInfo.getHashPath());
        FileHelper.createDir(serverInfo.getLogPath());

        System.out.println("Checking dependencies...");
        installDependencies(serverInfo);
        FileHelper.deleteDirectory(serverInfo.getDownloadPath());

        FileHelper.writeFile(serverInfo.getMetaPath().resolve("toolbox.json"), ConfigLoader.serializeToolboxInstall(serverInfo));
        System.out.println("Dependencies done");
    }

    private static void installDependencies(InstalledServerInfo serverInfo) {
        for (BranchConfig.Dependency dependency : serverInfo.getDependencies()) {

            if (dependency.location.startsWith("/"))
                dependency.location = dependency.location.substring(1);

            System.out.print("Checking " + dependency.name + "...");

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

        if (Files.exists(serverInfo.getLogPath(dependency))) {
            List<String> previousInstalledFiles = FileHelper.readLines(serverInfo.getLogPath(dependency));
            if (previousInstalledFiles != null) {
                for (String string : previousInstalledFiles) {
                    FileHelper.delete(Path.of(string));
                }
            }
        }

        List<String> installedFiles;
        if (dependency.unzip) {
            installedFiles = FileHelper.unzipFile(downloadPath, destination);
        } else {
            installedFiles = FileHelper.moveFile(downloadPath, destination.resolve(dependency.name));
        }

        installedFiles.add(destination.toString());
        FileHelper.writeFile(serverInfo.getHashPath(dependency), hash);
        FileHelper.writeLines(serverInfo.getLogPath(dependency), installedFiles);
    }
}