package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;

import java.nio.file.Path;

public class Paths {
    private static Path toolboxTempPath;
    private static Path installPath;

    public static void setToolboxTempPath(Path tempPath) {
        Paths.toolboxTempPath = tempPath;
    }

    public static void setInstallPath(Path installsPath, BranchConfig config) {
        Paths.installPath = installsPath.resolve(FileNameCleaner.cleanFileName(config.name));
    }

    public static Path getInstallPath() {
        return installPath;
    }

    public static Path getGlobalToolbox() {
        return toolboxTempPath;
    }

    public static Path getGlobalDownloadPath() {
        return getGlobalToolbox().resolve("downloads");
    }

    public static Path getGlobalDownloadPath(BranchConfig.Dependency dependency) {
        return getGlobalDownloadPath().resolve(FileNameCleaner.cleanFileName(dependency.name));
    }

    public static Path getInstallToolbox() {
        return getInstallPath().resolve(".toolbox");
    }

    public static Path getInstallTemp(BranchConfig.Dependency dependency, String extension) {
        return getInstallToolbox().resolve(FileNameCleaner.cleanFileName(dependency.name) + extension);
    }
}
