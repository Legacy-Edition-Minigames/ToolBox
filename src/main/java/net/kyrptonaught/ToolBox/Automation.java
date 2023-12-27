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
        if (CMDArgsParser.installServer()) {
            installServer();
            return;
        }

        InstalledServerInfo server = Menu.getServerFromName(CMDArgsParser.getTargetServer());

        if (server == null) {
            System.out.println();
            System.out.println("This server is invalid.");
            System.out.println("Returning to menu.");
            Menu.pressEnterToCont(input);
            return;
        }

        Menu.setState(Menu.State.EXISTING_INSTALL, server);

        if (CMDArgsParser.updateServer())
            Executer.updateServer(server);

        if (CMDArgsParser.launchServer())
            Executer.startServer(server);

    }

    public static void installServer() {
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

        InstalledServerInfo serverInfo = new InstalledServerInfo(branch, branchInfo);

        String name = CMDArgsParser.getTargetServer();
        if (name != null) serverInfo.setName(name);
        serverInfo.setPath();

        int allocatedRam = CMDArgsParser.getNewServerRam();
        if (allocatedRam < 1) allocatedRam = 3;
        serverInfo.setRAMArgs(allocatedRam);

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
