package net.kyrptonaught.ToolBox.holders;

import net.kyrptonaught.ToolBox.configs.BranchConfig;

import java.util.List;

public class InstalledDependencyInfo extends BranchConfig.Dependency {
    public String hash;
    public List<String> installedFiles;

    public InstalledDependencyInfo(BranchConfig.Dependency dependency) {
        this.name = dependency.name;
        this.displayName = dependency.displayName;
        this.url = dependency.url;
        this.location = dependency.location;
        this.gitRepo = dependency.gitRepo;
        this.unzip = dependency.unzip;
    }
}
