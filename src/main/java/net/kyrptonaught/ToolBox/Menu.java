package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Menu {

    public static void init(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        clearConsole();
        GUI(input);

        clearConsole();

        List<InstalledServerInfo> installedServers = detectInstalls();

        if (!installedServers.isEmpty()) {
            System.out.println("The following installs were detected");
            System.out.println("Select the server you would like to use, or NONE to set up a new server");
            System.out.println();

            for (int i = 0; i < installedServers.size(); i++) {
                InstalledServerInfo serverInfo = installedServers.get(i);
                System.out.println((i + 1) + ". " + serverInfo.getName() + " (" + serverInfo.getBranchInfo().name + ")");
            }
            System.out.println();
            System.out.println("0. NONE");

            System.out.println();
            System.out.print("Select Install: ");
            int selection = readInt(input) - 1;
            if (selection > -1) {
                existingInstallMenu(input, installedServers.get(selection));
            } else {
                installBranchMenu(input);
            }

        } else {
            installBranchMenu(input);
        }
    }

    public static void GUI(BufferedReader input) {
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
        System.out.println("Consider donating at Patreon! www.legacyminigames.xyz/patreon");
        System.out.println();
        System.out.println("Have fun!");
        System.out.println();

        pressEnterToCont(input);
    }

    public static void checkEula(BufferedReader input, InstalledServerInfo serverInfo) {
        Path eulaFile = serverInfo.getPath().resolve("eula.txt");
        boolean agreed = EulaChecker.checkEulaAgreement(eulaFile);

        if (agreed) return;

        System.out.println("""
                Do you accept the Minecraft's EULA?
                                
                For your server to run you must accept Minecraft's EULA.
                The Minecraft's EULA contains information and rules about what you can do and can't do while using the game.
                Agreement of the Minecraft's EULA is strictly needed, otherwise your server would be illegal to operate and thus, won't open.
                You can read the EULA here: https://aka.ms/MinecraftEULA
                WARNING: LEM WON'T WORK IF MINECRAFT'S EULA IS NOT AGREED!
                """);
        System.out.print("Do you want to accept the Minecraft's EULA? (Y/N): ");
        String eulaAgree = readLine(input).substring(0, 1).toUpperCase();

        if (eulaAgree.equals("Y")) {
            EulaChecker.agreeToEula(eulaFile);
            System.out.println("EULA accepted.");
        }
        System.out.println();
    }

    public static void installBranchMenu(BufferedReader input) {
        clearConsole();
        System.out.println("Checking for Branches");
        System.out.println();
        BranchesConfig branches = ConfigLoader.parseBranches(FileHelper.download("https://raw.githubusercontent.com/Legacy-Edition-Minigames/ToolBox/java/testConfigs/TestBranches.json"));
        System.out.println("Found the following Branches. Please enter the branch number you would like to use, or NONE to go back.");
        System.out.println();

        for (int i = 0; i < branches.branches.length; i++) {
            BranchesConfig.BranchInfo branch = branches.branches[i];
            System.out.println((i + 1) + ". " + branch.name + " : " + branch.desc);
        }
        System.out.println();
        System.out.println("0. NONE");

        System.out.println();
        System.out.print("Select Branch: ");
        int selection = readInt(input) - 1;
        System.out.println();

        if (selection > -1) {
            BranchesConfig.BranchInfo branchInfo = branches.branches[selection];
            System.out.println("Loading branch: " + branchInfo.name + " (" + branchInfo.url + ")");

            String url = GithubHelper.convertRepoToToolboxConfig(branchInfo.url);
            BranchConfig branch = ConfigLoader.parseToolboxConfig(FileHelper.download(url));

            if (branch == null) {
                System.out.println();
                System.out.println("This branch is invalid.");
                System.out.println("Returning to menu.");
                pressEnterToCont(input);
                init(null);
                return;
            }

            System.out.println();

            System.out.println("Please enter a name for this server, or leave blank for default (" + branch.name + "): ");
            System.out.println();
            System.out.print("Server Name: ");
            String enteredServerName = readLine(input);
            System.out.println();

            System.out.println("How much RAM do you want to allocate to the server?");
            System.out.println("It's recommended to use at least 3GB of RAM to ensure LEM will work as intended.");
            System.out.println();
            System.out.print("RAM Allocation (GB): ");
            int allocatedRam = readInt(input);
            System.out.println();

            //todo input sanitization
            //todo check if server with name already installed
            InstalledServerInfo serverInfo = new InstalledServerInfo(branch, branchInfo);
            if (!enteredServerName.isBlank()) serverInfo.setName(enteredServerName);
            serverInfo.setPath();
            serverInfo.setCustomLaunchArgs("-Xmx" + allocatedRam + "G -Xms" + allocatedRam + "G");

            System.out.println("Creating toolbox instance in " + serverInfo.getPath());
            Installer.installAndCheckForUpdates(serverInfo);
            System.out.println("Finished");
            System.out.println();
            checkEula(input, serverInfo);
            pressEnterToCont(input);

            existingInstallMenu(input, serverInfo);
        } else {
            init(null);
        }
    }

    public static void existingInstallMenu(BufferedReader input, InstalledServerInfo serverInfo) {
        clearConsole();
        BranchesConfig.BranchInfo info = serverInfo.getBranchInfo();
        System.out.println("Server Selected: ");
        System.out.println();
        System.out.println(serverInfo.getName() + " (" + info.name + ")");
        System.out.println(info.desc);
        System.out.println(info.url);
        System.out.println();
        System.out.println("""
                Chose an action below:
                                
                1. Start Server
                2. Check for Updates
                3. Reinstall
                4. Delete
                                
                0. Back
                 """);

        System.out.print("Action: ");
        int selectedAction = readInt(input);
        System.out.println();

        if (selectedAction == 1) {
            clearConsole();
            System.out.println("Starting server: " + serverInfo.getLaunchArgs());
            ServerRunner.runServer(serverInfo);
            System.out.println();
            System.out.println("Server Stopped...");
            System.out.println();
            pressEnterToCont(input);
        } else if (selectedAction == 2) {
            //todo remove old dependencies
            Installer.installAndCheckForUpdates(serverInfo);
            System.out.println();
            System.out.println("Server updated.");
            System.out.println();
            pressEnterToCont(input);
            existingInstallMenu(input, serverInfo);
        } else if (selectedAction == 3) {
            FileHelper.deleteDirectory(serverInfo.getPath());
            System.out.println("Server deleted...reinstalling...");
            System.out.println();
            Installer.installAndCheckForUpdates(serverInfo);
            System.out.println();
            checkEula(input, serverInfo);
            System.out.println("Server reinstalled.");
            System.out.println();
            pressEnterToCont(input);
            existingInstallMenu(input, serverInfo);
        } else if (selectedAction == 4) {
            FileHelper.deleteDirectory(serverInfo.getPath());
            System.out.println("Server deleted.");
            System.out.println();
            pressEnterToCont(input);
        }

        //todo properly return to main menu
        init(null);
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                System.out.print("\033\143");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pressEnterToCont(BufferedReader input) {
        System.out.print("Press ENTER to continue...");
        readLine(input);
    }

    public static String readLine(BufferedReader input) {
        try {
            return input.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int readInt(BufferedReader input) {
        try {
            return Integer.parseInt(input.readLine());
        } catch (NumberFormatException numberFormatException) {
            System.out.print("Please enter a number: ");
            return readInt(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<InstalledServerInfo> detectInstalls() {
        Path installPath = Path.of("installs");

        List<InstalledServerInfo> configs = new ArrayList<>();
        try (Stream<Path> files = Files.walk(installPath)) {
            files.forEach(path -> {
                if (path.endsWith(Path.of(".toolbox").resolve("meta").resolve("toolbox.json"))) {
                    //if (path.toString().endsWith("\\.toolbox\\meta\\toolbox.json")) {
                    configs.add(ConfigLoader.parseToolboxInstall(FileHelper.readFile(path)));
                }
            });
        } catch (IOException ignored) {
        }

        return configs;
    }
}
