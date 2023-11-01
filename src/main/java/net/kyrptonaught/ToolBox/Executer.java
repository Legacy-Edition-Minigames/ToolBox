package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.IO.ConfigLoader;
import net.kyrptonaught.ToolBox.IO.FileHelper;
import net.kyrptonaught.ToolBox.IO.GithubHelper;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.holders.InstalledServerInfo;
import net.kyrptonaught.ToolBox.holders.RunningServer;

public class Executer {

    public static void startServer(InstalledServerInfo serverInfo) {
        System.out.println("Starting server: " + serverInfo.getLaunchArgs());
        RunningServer runningServer = ServerRunner.runServer(serverInfo);

        System.out.println();
        if (runningServer.isRunning()) {
            System.out.println("Server backgrounded...");

        } else {
            System.out.println("Server stopped...");

            if (CMDArgsParser.autoRestart()) {
                startServer(serverInfo);
            }

            if (CMDArgsParser.autoExit()) {
                Menu.setState(Menu.State.EXIT);
                return;
            }
        }
        System.out.println();
    }

    public static void updateServer(InstalledServerInfo serverInfo) {
        String url = GithubHelper.convertRepoToToolboxConfig(serverInfo.getBranchInfo().url);
        BranchConfig branch = ConfigLoader.parseToolboxConfig(FileHelper.download(url));
        serverInfo.updateBranchConfig(branch);
        Installer.installAndCheckForUpdates(serverInfo);
    }
}
