package net.kyrptonaught.ToolBox.holders;

import net.kyrptonaught.ToolBox.IO.FileNameCleaner;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.nio.file.Path;

public class InstalledServerInfo {

    private BranchConfig branchConfig;

    private final BranchesConfig.BranchInfo branchInfo;

    private transient Path installPath;

    private String installName;

    private String customLaunchArgs;

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

    public void setCustomLaunchArgs(String args) {
        customLaunchArgs = args;
    }

    public String getLaunchArgs() {
        if (customLaunchArgs != null) {
            int space = branchConfig.launchCMD.indexOf(" ");
            return branchConfig.launchCMD.substring(0, space) + " " + customLaunchArgs + branchConfig.launchCMD.substring(space);
        }
        return branchConfig.launchCMD;
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

    public Path getToolBox() {
        return getPath().resolve(".toolbox");
    }

    public Path getDownloadPath() {
        return getToolBox().resolve("downloads");
    }

    public Path getDownloadPath(BranchConfig.Dependency dependency) {
        return getDownloadPath().resolve(FileNameCleaner.cleanFileName(dependency.name));
    }

    public Path getHashPath() {
        return getToolBox().resolve("hash");
    }

    public Path getHashPath(BranchConfig.Dependency dependency) {
        return getHashPath().resolve(FileNameCleaner.cleanFileName(dependency.name) + ".hash");
    }

    public Path getLogPath() {
        return getToolBox().resolve("log");
    }

    public Path getLogPath(BranchConfig.Dependency dependency) {
        return getLogPath().resolve(FileNameCleaner.cleanFileName(dependency.name) + ".installed");
    }

    public Path getMetaPath() {
        return getToolBox().resolve("meta");
    }

    public Path getDependencyPath(BranchConfig.Dependency dependency) {
        return getPath().resolve(FileNameCleaner.removeFirstSlashAndClean(dependency.location));
    }

    public Path getTempLocation() {
        return getToolBox().resolve("temp");
    }

    public Path getTempLocation(BranchConfig.Dependency dependency) {
        return getTempLocation().resolve(FileNameCleaner.cleanFileName(dependency.name));
    }
}
