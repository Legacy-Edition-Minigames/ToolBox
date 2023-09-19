package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Menu {

    public static State state;
    public static Object stateData;

    public static void startStateMachine(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println();
            System.out.println("SHUTTING DOWN");
            System.out.println("Attempting to stop running servers");
            ServerRunner.exit();

        }));
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        setState(State.SPLASH);

        while (true) {
            clearConsole();
            switch (state) {
                case SPLASH -> splashState(input);
                case MENU -> menuState(input);
                case EXISTING_INSTALL -> existingMenu(input);
                case RUNNING_INSTALL -> runningMenu(input);
                case INSTALLER -> installMenu(input);
                case EXIT -> System.exit(0);
            }
        }
    }

    public static void splashState(BufferedReader input) {
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
        System.out.println("LEM-ToolBox 2.0 created by Kyrptonaught");
        System.out.println("Legacy Edition Minigames created by DBTDerpbox & Kyrptonaught");
        System.out.println("Consider donating at Patreon! www.legacyminigames.xyz/patreon");
        System.out.println();
        System.out.println("Have fun!");
        System.out.println();

        pressEnterToCont(input);

        setState(State.MENU);
    }

    public static void menuState(BufferedReader input) {
        System.out.println("Loading Servers...");
        System.out.println();

        List<InstalledServerInfo> installedServers = detectInstalls();
        List<RunningServer> runningServers = ServerRunner.getRunningServers();
        BranchesConfig branches = ConfigLoader.parseBranches(FileHelper.download("https://raw.githubusercontent.com/Legacy-Edition-Minigames/ToolBox/java/testConfigs/TestBranches.json"));

        HashMap<Integer, Runnable> options = new HashMap<>();

        clearConsole();

        System.out.println("Select the server you would like to use");
        int serverOptions = 0;

        System.out.println();
        System.out.println("Installed servers");
        if (!installedServers.isEmpty()) {
            for (InstalledServerInfo serverInfo : installedServers) {
                serverOptions++;
                System.out.println(serverOptions + ". " + serverInfo.getName() + " (" + serverInfo.getBranchInfo().name + ")");
                options.put(serverOptions, () -> setState(State.EXISTING_INSTALL, serverInfo));
            }
        } else {
            System.out.println("--NONE--");
        }

        System.out.println();
        System.out.println("Running servers");
        if (!runningServers.isEmpty()) {
            for (RunningServer runningServer : runningServers) {
                serverOptions++;
                System.out.println(serverOptions + ". " + runningServer.serverInfo.getName() + " (" + runningServer.serverInfo.getBranchInfo().name + ")");
                options.put(serverOptions, () -> setState(State.RUNNING_INSTALL, runningServer));
            }
        } else {
            System.out.println("--NONE--");
        }

        System.out.println();
        System.out.println("New servers");
        if (!branches.branches.isEmpty()) {
            for (BranchesConfig.BranchInfo branch : branches.branches) {
                serverOptions++;
                System.out.println(serverOptions + ". " + branch.name + " : " + branch.desc);
                options.put(serverOptions, () -> setState(State.INSTALLER, branch));
            }
        } else {
            System.out.println("--NONE--");
        }
        System.out.println();
        System.out.println("Other Options");
        System.out.println("0. Exit");

        options.put(0, () -> setState(State.EXIT));

        System.out.println();

        System.out.print("Select Server: ");
        int selection = readInt(input);


        if (options.containsKey(selection)) {
            options.get(selection).run();
        }
        //will loop back into this menu
    }

    public static void existingMenu(BufferedReader input) {
        InstalledServerInfo serverInfo = (InstalledServerInfo) stateData;

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


        if (selectedAction == 0) {
            setState(State.MENU);
        } else if (selectedAction == 1) {
            clearConsole();
            System.out.println("Starting server: " + serverInfo.getLaunchArgs());
            RunningServer runningServer = ServerRunner.runServer(serverInfo);

            System.out.println();
            if (runningServer.isRunning()) {
                System.out.println("Server backgrounded...");

            } else {
                System.out.println("Server stopped...");
            }
            System.out.println();

            setState(State.MENU);
            pressEnterToCont(input);
        } else if (selectedAction == 2) {
            //todo remove old dependencies
            Installer.installAndCheckForUpdates(serverInfo);
            System.out.println();
            System.out.println("Server updated.");
            System.out.println();
            pressEnterToCont(input);
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
        } else if (selectedAction == 4) {
            FileHelper.deleteDirectory(serverInfo.getPath());
            System.out.println("Server deleted.");
            System.out.println();
            pressEnterToCont(input);
            setState(State.MENU);
        }
        //will loop back into this menu
    }

    public static void runningMenu(BufferedReader input) {
        RunningServer runningServer = (RunningServer) stateData;

        BranchesConfig.BranchInfo info = runningServer.serverInfo.getBranchInfo();
        System.out.println("Server Selected: ");
        System.out.println();
        System.out.println(runningServer.serverInfo.getName() + " (" + info.name + ")");
        System.out.println(info.desc);
        System.out.println(info.url);
        System.out.println();
        System.out.println("""
                Chose an action below:
                                
                1. Open Console
                2. Stop Server
                                
                0. Back
                 """);

        System.out.print("Action: ");
        int selectedAction = readInt(input);
        System.out.println();

        if (selectedAction == 0) {
            setState(State.MENU);
        } else if (selectedAction == 1) {
            clearConsole();
            System.out.println("Resuming server");
            ServerRunner.resumeServer(runningServer);

            System.out.println();
            if (runningServer.isRunning()) {
                System.out.println("Server backgrounded...");

            } else {
                System.out.println("Server stopped...");
            }
            System.out.println();

            setState(State.MENU);
            pressEnterToCont(input);
        } else if (selectedAction == 2) {
            System.out.println();
            System.out.println("Stopping Server...");

            ServerRunner.stopServer(runningServer);
            System.out.println();
            System.out.println("Server Stopped...");
            System.out.println();
            setState(State.MENU);
            pressEnterToCont(input);
        }
    }

    public static void installMenu(BufferedReader input) {
        BranchesConfig.BranchInfo branchInfo = (BranchesConfig.BranchInfo) stateData;

        System.out.println("Loading branch: " + branchInfo.name + " (" + branchInfo.url + ")");

        String url = GithubHelper.convertRepoToToolboxConfig(branchInfo.url);
        BranchConfig branch = ConfigLoader.parseToolboxConfig(FileHelper.download(url));

        if (branch == null) {
            System.out.println();
            System.out.println("This branch is invalid.");
            System.out.println("Returning to menu.");
            pressEnterToCont(input);

            setState(State.MENU);
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
        if (allocatedRam < 1) allocatedRam = 3;
        serverInfo.setCustomLaunchArgs("-Xmx" + allocatedRam + "G -Xms" + allocatedRam + "G");

        System.out.println("Creating toolbox instance in " + serverInfo.getPath());
        Installer.installAndCheckForUpdates(serverInfo);
        System.out.println("Finished");
        System.out.println();
        checkEula(input, serverInfo);
        pressEnterToCont(input);

        setState(State.EXISTING_INSTALL, serverInfo);

        //will loop back into this menu
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
        try (Stream<Path> files = Files.walk(installPath, 1)) {
            files.forEach(path -> {
                if (Files.isDirectory(path) && Files.exists(path.resolve(".toolbox").resolve("meta").resolve("toolbox.json"))) {
                    configs.add(ConfigLoader.parseToolboxInstall(FileHelper.readFile(path.resolve(".toolbox").resolve("meta").resolve("toolbox.json"))));
                }
            });
        } catch (IOException ignored) {
        }

        return configs;
    }

    public static void setState(State state) {
        setState(state, null);
    }

    public static void setState(State state, Object stateData) {
        Menu.state = state;
        Menu.stateData = stateData;
    }

    public enum State {
        SPLASH,
        MENU,
        EXISTING_INSTALL,
        RUNNING_INSTALL,
        INSTALLER,
        EXIT
    }
}