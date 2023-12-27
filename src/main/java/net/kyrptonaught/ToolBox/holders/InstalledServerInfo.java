package net.kyrptonaught.ToolBox.holders;

import net.kyrptonaught.ToolBox.IO.FileNameCleaner;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Pattern;

public class InstalledServerInfo {

    private BranchConfig branchConfig;

    private final BranchesConfig.BranchInfo branchInfo;

    private transient Path installPath;

    private String installName;

    private final HashMap<String, String> launchARGS = new HashMap<>();

    public InstalledServerInfo(BranchConfig branchConfig, BranchesConfig.BranchInfo branchInfo) {
        this.branchConfig = branchConfig;
        this.branchInfo = branchInfo;
        this.installName = branchConfig.name;
    }

    public void updateBranchConfig(BranchConfig config) {
        this.branchConfig = config;
    }

    public String getName() {
        if (installName != null)
            return installName;
        return branchConfig.name;
    }

    public void setName(String name) {
        installName = name;
    }

    public void setRAMArgs(int ram) {
        setCustomLaunchArgs("<ram>", "-Xmx" + ram + "G -Xms" + ram + "G");
    }

    public void setCustomLaunchArgs(String key, String args) {
        launchARGS.put(key, args);
    }

    public String getLaunchCMD() {
        return Pattern.compile("<[a-zA-Z0-9]+>")
                .matcher(branchConfig.launchCMD)
                .replaceAll(matchResult -> launchARGS.getOrDefault(matchResult.group(), ""))
                .replaceAll("\\s{2}", " "); //remove double spaces from blank substitutions
    }

    public BranchConfig.Dependency[] getDependencies() {
        return branchConfig.dependencies;
    }

    public BranchesConfig.BranchInfo getBranchInfo() {
        return branchInfo;
    }

    public void setPath(Path path) {
        this.installPath = path;
    }

    public void setPath() {
        setPath(Path.of("installs").resolve(FileNameCleaner.cleanFileName(getName())));
    }

    public Path getPath() {
        return installPath;
    }

    public Path getDependencyInstallPath(BranchConfig.Dependency dependency) {
        return getPath().resolve(FileNameCleaner.removeFirstSlashAndClean(dependency.location));
    }

    public Path getToolBoxPath() {
        return getPath().resolve(".toolbox");
    }

    public Path getDownloadPath() {
        return getToolBoxPath().resolve("downloads");
    }

    public Path getDownloadPath(BranchConfig.Dependency dependency) {
        return getDownloadPath().resolve(FileNameCleaner.cleanFileName(dependency.name));
    }

    public Path getInstalledDependencyPath() {
        return getToolBoxPath().resolve("dependencies");
    }

    public Path getInstalledDependencyPath(BranchConfig.Dependency dependency) {
        return getInstalledDependencyPath().resolve(FileNameCleaner.cleanFileName(dependency.name) + ".json");
    }

    public Path getMetaPath() {
        return getToolBoxPath().resolve("meta");
    }

    public Path getTempPath() {
        return getToolBoxPath().resolve("temp");
    }

    public Path getTempPath(BranchConfig.Dependency dependency) {
        return getTempPath().resolve(FileNameCleaner.cleanFileName(dependency.name));
    }
}
