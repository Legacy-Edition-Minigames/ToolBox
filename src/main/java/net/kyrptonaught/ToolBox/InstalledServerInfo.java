package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.nio.file.Path;

public class InstalledServerInfo {

    private final BranchConfig branchConfig;

    private final BranchesConfig.BranchInfo branchInfo;

    private Path installPath;

    private String installName;

    public InstalledServerInfo(BranchConfig branchConfig, BranchesConfig.BranchInfo branchInfo) {
        this.branchConfig = branchConfig;
        this.branchInfo = branchInfo;
        this.installName = branchConfig.name;
    }

    public String getName() {
        if (installName != null)
            return installName;
        return branchConfig.name;
    }

    public void setName(String name) {
        installName = name;
    }

    public String getLaunchArgs() {
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
}
