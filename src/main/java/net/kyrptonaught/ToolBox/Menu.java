package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.util.Scanner;

public class Menu {

    public static void init(String[] args) {
        Scanner scan = new Scanner(System.in);
        GUI();
        System.out.println("Checking for Branches");
        BranchesConfig branches = ConfigLoader.parseBranches(FileHelper.download("https://raw.githubusercontent.com/Legacy-Edition-Minigames/ToolBox/java/testConfigs/TestBranches.json"));
        System.out.println("Found the following Branches. Please enter the branch number you would like to use");

        for (int i = 0; i < branches.branches.length; i++) {
            BranchesConfig.BranchInfo branch = branches.branches[i];
            System.out.println((i + 1) + ": " + branch.name + " : " + branch.desc);
        }

        System.out.print("Select Branch: ");
        int selection = scan.nextInt() - 1;
        BranchesConfig.BranchInfo branchInfo = branches.branches[selection];
        System.out.println("Loading branch: " + branchInfo.name + "(" + branchInfo.url + ")");
        String url = GithubHelper.convertRepoToToolboxConfig(branchInfo.url);
        BranchConfig branch = ConfigLoader.parseToolboxConfig(FileHelper.download(url));
        System.out.println("Creating toolbox instance in /installs/" + branch.name);
        Installer.installBranch(branch);
        System.out.println("Finished");
        System.out.print("Start Server? (Y/N): ");
        String input = scan.next().substring(0, 1).toUpperCase();

        if (input.equals("Y")) {
            System.out.println("Starting server: " + branch.launchCMD);
            ServerRunner.runServer(branch);
            System.out.println("Server Stopped...Exiting");
        }
    }

    public static void GUI() {
        System.out.println(" ▄▄▄     ▄▄▄▄▄▄▄ ▄▄   ▄▄    ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄▄     ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄   ▄▄ ");
        System.out.println("█   █   █       █  █▄█  █  █       █       █       █   █   █  ▄    █       █  █▄█  █");
        System.out.println("█   █   █    ▄▄▄█       █  █▄     ▄█   ▄   █   ▄   █   █   █ █▄█   █   ▄   █       █");
        System.out.println("█   █   █   █▄▄▄█       █    █   █ █  █ █  █  █ █  █   █   █       █  █ █  █       █");
        System.out.println("█   █▄▄▄█    ▄▄▄█       █    █   █ █  █▄█  █  █▄█  █   █▄▄▄█  ▄   ██  █▄█  ██     █ ");
        System.out.println("█       █   █▄▄▄█ ██▄██ █    █   █ █       █       █       █ █▄█   █       █   ▄   █");
        System.out.println("█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄█   █▄█    █▄▄▄█ █▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄█ █▄▄█");
        System.out.println();
    }
}
