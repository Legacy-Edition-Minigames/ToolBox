package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.holders.InstalledServerInfo;

public class Automation {

    public static void run() {
        String serverName = CMDArgsParser.getTargetServer();
        if (serverName == null) return;

        InstalledServerInfo server = Menu.getServerFromName(serverName);
        Menu.setState(Menu.State.EXISTING_INSTALL, server);

        if (CMDArgsParser.updateServer())
            Executer.updateServer(server);

        if (CMDArgsParser.launchServer())
            Executer.startServer(server);
    }
}
