package net.kyrptonaught.ToolBox;

import com.google.gson.JsonObject;
import net.kyrptonaught.ToolBox.configs.BranchConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Installer {

    public static void installBranch(BranchConfig branch) {
        Paths.setToolboxTempPath(Path.of(".toolbox"));
        Paths.setInstallPath(Path.of("installs"), branch);

        FileHelper.createDir(Paths.getInstallToolbox());
        FileHelper.createDir(Paths.getGlobalDownloadPath());


        System.out.println("Checking dependencies...");
        installDependencies(branch);

        System.out.println("Dependencies done");
        FileHelper.deleteDirectory(Paths.getGlobalToolbox());
    }

    public static String getNewHash(BranchConfig.Dependency dependency) {
        if (dependency.gitRepo) {

            String apiCall = GithubHelper.convertRepoToApiCall(dependency.url);
            dependency.url = GithubHelper.convertRepoToZipball(dependency.url);//todo move this conversion to the dl

            JsonObject response = FileHelper.download(apiCall, JsonObject.class);
            return response.getAsJsonObject("commit").getAsJsonPrimitive("sha").getAsString();
        } else {
            Path tempFile = Paths.getGlobalDownloadPath(dependency);
            FileHelper.download(dependency.url, tempFile);
            String hash = FileHelper.hashFile(tempFile);
            FileHelper.delete(tempFile);
            return hash;
        }
    }

    public static String hashExistingFile(BranchConfig.Dependency dependency) {
        Path hashFile = Paths.getInstallTemp(dependency, ".hash");
        if (Files.exists(hashFile) && Files.isReadable(hashFile))
            return FileHelper.readHash(hashFile);
        return null;
    }

    public static void installFile(BranchConfig.Dependency dependency, String hash) {
        Path destination = Paths.getInstallPath().resolve(FileNameCleaner.removeFirstSlashAndClean(dependency.location));
        FileHelper.createDir(destination);

        FileHelper.download(dependency.url, Paths.getGlobalDownloadPath(dependency));

        List<String> installedFiles;
        if (dependency.unzip) {
            installedFiles = FileHelper.unzipFile(Paths.getGlobalDownloadPath(dependency), destination);
            FileHelper.delete(Paths.getGlobalDownloadPath(dependency));
        } else {
            installedFiles = FileHelper.moveFile(Paths.getGlobalDownloadPath(dependency), destination.resolve(dependency.name));
        }

        installedFiles.add(destination.toString());
        FileHelper.writeHash(Paths.getInstallTemp(dependency, ".hash"), hash);
        FileHelper.writeLines(Paths.getInstallTemp(dependency, ".installed"), installedFiles);
    }


    public static void installDependencies(BranchConfig branch) {
        for (BranchConfig.Dependency dependency : branch.dependencies) {

            if (dependency.location.startsWith("/"))
                dependency.location = dependency.location.substring(1);

            System.out.print("Checking " + dependency.name + "...");

            String hash = getNewHash(dependency);
            String existingHash = hashExistingFile(dependency);

            if (hash != null && !hash.equals(existingHash)) {
                System.out.print("downloading...");
                installFile(dependency, hash);
                System.out.println("Installed");
            } else {
                System.out.println("Already exists");
            }
        }
    }
}
