package net.kyrptonaught.ToolBox;

import java.util.ArrayList;
import java.util.List;

public class ServerRunner {

    private static final List<RunningServer> runningServers = new ArrayList<>();

    public static void exit() {
        for (RunningServer server : runningServers) {
            server.stop();
        }
    }

    public static RunningServer runServer(InstalledServerInfo serverInfo) {
        RunningServer server = new RunningServer();
        runningServers.add(server);
        server.startServer(serverInfo);
        server.setActive();
        return server;
    }

    public static void resumeServer(RunningServer server) {
        server.setActive();
    }

    public static void killServer(RunningServer server) {
        server.kill();
        runningServers.remove(server);
    }

    public static void stopServer(RunningServer server) {
        server.stop();
        runningServers.remove(server);
    }

    public static List<RunningServer> getRunningServers() {
        return runningServers;
    }
}
