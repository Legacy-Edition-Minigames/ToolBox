package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Menu {

    public static void init(String[] args) {
        Scanner scan = new Scanner(System.in);
        clearConsole();
        GUI(scan);

        clearConsole();

        List<InstalledServerInfo> installedServers = detectInstalls();

        if (!installedServers.isEmpty()) {
            System.out.println("The following installs were detected");
            System.out.println("Select the server you would like to use, or NONE to set up a new server");
            System.out.println();

            System.out.println("0: NONE");
            for (int i = 0; i < installedServers.size(); i++) {
                InstalledServerInfo serverInfo = installedServers.get(i);
                System.out.println((i + 1) + ": " + serverInfo.getName());
            }

            System.out.println();
            System.out.print("Select Install: ");
            int selection = scan.nextInt() - 1;
            if (selection > -1) {
                existingInstallMenu(scan, installedServers.get(selection));
            } else {
                installBranchMenu(scan);
            }

        } else {
            installBranchMenu(scan);
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

        pressEnterToCont(scan);
    }

    public static void checkEula(Scanner scan, InstalledServerInfo serverInfo) {
        Path eulaFile = serverInfo.getPath().resolve("eula.txt");
        boolean agreed = EulaChecker.checkEulaAgreement(eulaFile);

        if (agreed) return;

        System.out.println("""
                Do you accept the Minecraft's EULA?
                                
                For your server to run you must accept Minecraft's EULA.
                The Minecraft's EULA contains information and rules about what you can do and can't do while using the game.
                Agreement of the Minecraft's EULA is strictly needed, otherwise your server would be illegal to operate and thus, won't open.
                WARNING: LEM WON'T WORK IF MINECRAFT'S EULA IS NOT AGREED!
                """);
        System.out.print("Do you want to accept the Minecraft's EULA? (Y/N): ");
        String input = scan.next().substring(0, 1).toUpperCase();

        if (input.equals("Y")) {
            EulaChecker.agreeToEula(eulaFile);
            System.out.println("EULA accepted.");
        }
    }

    public static void installBranchMenu(Scanner scan) {
        clearConsole();
        System.out.println("Checking for Branches");
        System.out.println();
        BranchesConfig branches = ConfigLoader.parseBranches(FileHelper.download("https://raw.githubusercontent.com/Legacy-Edition-Minigames/ToolBox/java/testConfigs/TestBranches.json"));
        System.out.println("Found the following Branches. Please enter the branch number you would like to use");
        System.out.println();

        for (int i = 0; i < branches.branches.length; i++) {
            BranchesConfig.BranchInfo branch = branches.branches[i];
            System.out.println((i + 1) + ": " + branch.name + " : " + branch.desc);
        }

        System.out.println();
        System.out.print("Select Branch: ");
        int selection = scan.nextInt() - 1;

        BranchesConfig.BranchInfo branchInfo = branches.branches[selection];
        System.out.println("Loading branch: " + branchInfo.name + " (" + branchInfo.url + ")");

        String url = GithubHelper.convertRepoToToolboxConfig(branchInfo.url);
        BranchConfig branch = ConfigLoader.parseToolboxConfig(FileHelper.download(url));

        InstalledServerInfo serverInfo = new InstalledServerInfo(branch, branchInfo);
        serverInfo.setPath();

        System.out.println("Creating toolbox instance in " + serverInfo.getPath());
        Installer.installAndCheckForUpdates(serverInfo);
        System.out.println("Finished");
        System.out.println();
        checkEula(scan, serverInfo);
        pressEnterToCont(scan);


        existingInstallMenu(scan, serverInfo);
    }

    public static void existingInstallMenu(Scanner scan, InstalledServerInfo serverInfo) {
        clearConsole();
        BranchesConfig.BranchInfo info = serverInfo.getBranchInfo();
        System.out.println("Server Selected: ");
        System.out.println(info.name);
        System.out.println(info.desc);
        System.out.println(info.url);
        System.out.println();
        System.out.println("""
                Chose an action below:
                                
                1. Start Server
                2. Check for Updates
                3. Reinstall
                4. Delete
                                
                5. Back
                 """);

        System.out.print("Action: ");
        int input = scan.nextInt();

        if (input == 1) {
            clearConsole();
            System.out.println("Starting server: " + serverInfo.getLaunchArgs());
            ServerRunner.runServer(serverInfo);
            System.out.println("Server Stopped...Exiting");
        } else if (input == 2) {
            Installer.installAndCheckForUpdates(serverInfo);
            existingInstallMenu(scan, serverInfo);
        } else if (input == 3) {
            FileHelper.deleteDirectory(serverInfo.getPath());
            Installer.installAndCheckForUpdates(serverInfo);
            checkEula(scan, serverInfo);
            existingInstallMenu(scan, serverInfo);
        } else if (input == 4) {
            FileHelper.deleteDirectory(serverInfo.getPath());
        }

        //todo properly return to main menu
        init(null);
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

    public static void pressEnterToCont(Scanner scan) {
        System.out.println("Press ENTER to continue...");
        scan.nextLine();
    }

    public static List<InstalledServerInfo> detectInstalls() {
        Path installPath = Path.of("installs");

        List<InstalledServerInfo> configs = new ArrayList<>();
        try (Stream<Path> files = Files.walk(installPath)) {
            files.forEach(path -> {
                if (path.toString().endsWith("\\.toolbox\\meta\\toolbox.json")) {
                    configs.add(ConfigLoader.parseToolboxInstall(FileHelper.readFile(path)));
                }
            });
        } catch (IOException ignored) {
        }

        return configs;
    }
}