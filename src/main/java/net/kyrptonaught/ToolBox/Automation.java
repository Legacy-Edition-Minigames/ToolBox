package net.kyrptonaught.ToolBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

import net.kyrptonaught.ToolBox.Menu.State;
import net.kyrptonaught.ToolBox.IO.ConfigLoader;
import net.kyrptonaught.ToolBox.IO.EulaChecker;
import net.kyrptonaught.ToolBox.IO.FileHelper;
import net.kyrptonaught.ToolBox.IO.GithubHelper;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;
import net.kyrptonaught.ToolBox.holders.InstalledServerInfo;

public class Automation {
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public static void run() {
        String serverName = CMDArgsParser.getTargetServer();
        if (serverName == null) return;

        InstalledServerInfo server = Menu.getServerFromName(serverName);
        Menu.setState(Menu.State.EXISTING_INSTALL, server);

        if (CMDArgsParser.updateServer())
            Executer.updateServer(server);

        if (CMDArgsParser.launchServer())
            Executer.startServer(server);
        
        if (CMDArgsParser.installServer()) {
            BranchesConfig.BranchInfo branchInfo = CMDArgsParser.getNewServerBranch();

            String url = GithubHelper.convertRepoToToolboxConfig(branchInfo.url);
            BranchConfig branch = ConfigLoader.parseToolboxConfig(FileHelper.download(url));

            if (branch == null) {
                System.out.println();
                System.out.println("This branch is invalid.");
                System.out.println("Returning to menu.");
                Menu.pressEnterToCont(input);

                return;
            }

            String name = CMDArgsParser.getTargetServer();

            int allocatedRam = CMDArgsParser.getNewServerRam();

            InstalledServerInfo serverInfo = new InstalledServerInfo(branch, branchInfo);
            serverInfo.setName(name);
            serverInfo.setPath();
            if (allocatedRam < 1) allocatedRam = 3;
            serverInfo.setCustomLaunchArgs("-Xmx" + allocatedRam + "G -Xms" + allocatedRam + "G");

            Installer.installAndCheckForUpdates(serverInfo);
            if (CMDArgsParser.doesNewServerAgreeToEULA()) {
                Path eulaFile = serverInfo.getPath().resolve("eula.txt");

                EulaChecker.agreeToEula(eulaFile);
            } else if (!CMDArgsParser.unattendedInstall()) {
                Menu.checkEula(input, serverInfo);
            } else System.out.println("Did not agree to Mojang's EULA");

            if (CMDArgsParser.unattendedInstall()) {
                Menu.setState(State.EXIT);
            } else {
                Menu.pressEnterToCont(input);

                Menu.setState(State.EXISTING_INSTALL, serverInfo);
            }
        }
    }
}
