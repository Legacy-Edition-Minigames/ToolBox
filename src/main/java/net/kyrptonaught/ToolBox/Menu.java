package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.nio.file.Path;
import java.util.Scanner;

public class Menu {

    public static void init(String[] args) {
        Scanner scan = new Scanner(System.in);
        clearConsole();
        GUI(scan);


        clearConsole();
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

        clearConsole();
        checkEula(scan);

        System.out.print("Start Server? (Y/N): ");
        String input = scan.next().substring(0, 1).toUpperCase();

        if (input.equals("Y")) {
            System.out.println("Starting server: " + branch.launchCMD);
            ServerRunner.runServer(branch);
            System.out.println("Server Stopped...Exiting");
        }
    }

    public static void GUI(Scanner scan) {
        System.out.println(" ▄▄▄     ▄▄▄▄▄▄▄ ▄▄   ▄▄    ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄▄     ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄   ▄▄ ");
        System.out.println("█   █   █       █  █▄█  █  █       █       █       █   █   █  ▄    █       █  █▄█  █");
        System.out.println("█   █   █    ▄▄▄█       █  █▄     ▄█   ▄   █   ▄   █   █   █ █▄█   █   ▄   █       █");
        System.out.println("█   █   █   █▄▄▄█       █    █   █ █  █ █  █  █ █  █   █   █       █  █ █  █       █");
        System.out.println("█   █▄▄▄█    ▄▄▄█       █    █   █ █  █▄█  █  █▄█  █   █▄▄▄█  ▄   ██  █▄█  ██     █ ");
        System.out.println("█       █   █▄▄▄█ ██▄██ █    █   █ █       █       █       █ █▄█   █       █   ▄   █");
        System.out.println("█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄█   █▄█    █▄▄▄█ █▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄█ █▄▄█");
        System.out.println();
        System.out.println("Welcome to LEM-ToolBox!");
        System.out.println();
        System.out.println("LEM-ToolBox is a tool designed to make it easy for users to install, update and customize their own LEB instance.");
        System.out.println();
        System.out.println("If you encounter any problem, try performing a clean reinstall or contact us on our Discord.");
        System.out.println("LEM-ToolBox created by Kyrptonaught");
        System.out.println("Legacy Edition Minigames created by DBTDerpbox & Kyrptonaught");
        System.out.println("Consider donating at Patreon! patreon.com/DBTDerpbox");
        System.out.println();
        System.out.println("Have fun!");
        System.out.println();

        System.out.println("Press ENTER to continue...");
        scan.nextLine();
    }

    public static void checkEula(Scanner scan) {
        Path eulaFile = Paths.getInstallPath().resolve("eula.txt");
        boolean agreed = EulaChecker.checkEulaAgreement(eulaFile);

        if (agreed) return;

        System.out.println("Do you accept the Minecraft's EULA?");
        System.out.println("For your server to run you must accept Minecraft's EULA.");
        System.out.println("The Minecraft's EULA contains information and rules about what you can do and can't do while using the game.");
        System.out.println("Agreement of the Minecraft's EULA is strictly needed, otherwise your server would be illegal to operate and thus, won't open.");
        System.out.println();
        System.out.println("WARNING: LEB WON'T WORK IF MINECRAFT'S EULA IS NOT AGREED!");
        System.out.println();
        System.out.print("Do you want to accept the Minecraft's EULA? (Y/N): ");
        String input = scan.next().substring(0, 1).toUpperCase();

        if (input.equals("Y")) {
            EulaChecker.agreeToEula(eulaFile);
        }
    }

    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
