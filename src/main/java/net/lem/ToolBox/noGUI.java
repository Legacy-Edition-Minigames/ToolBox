package net.lem.ToolBox;

import java.io.IOException;
import java.util.Objects;

public class noGUI {

    public static void GUI() {
        //FileDownloader.downloadAndUnzip("https://github.com/Legacy-Edition-Minigames/Minigames/archive/refs/heads/experimental-server.zip", "serverInstance");
        //download branches
        ConfigLoader.BranchConfig branches = ConfigLoader.parseBranches(FileDownloader.download(Conf.get(2) + "/DefaultBranches.json"));

        /*
        branches.branches.forEach((s, s2) -> {
            System.out.println(s + " : " + s2);
        });
        */


        ConfigLoader.ToolboxConfig toolboxConfig = ConfigLoader.parseToolboxConfig(FileDownloader.download(Conf.get(2) + "/" + Conf.get(0) + "/toolboxConfig.json"));
        if (!toolboxConfig.version.equals(Conf.get(1))) {
            //download server jar
            FileDownloader.download(toolboxConfig.serverJarURL, "serverInstance/server.jar");

            //download minigames repo
            FileDownloader.downloadAndUnzip(toolboxConfig.LEMbaseURL, "serverInstance");

            //download mods
            toolboxConfig.mods.forEach((s, s2) -> {
                FileDownloader.download(s2, "serverInstance/mods/" + s + ".jar");
            });

            FileDownloader.download(Conf.get(2) + "/" + Conf.get(0) + "/" + toolboxConfig.DEFCRCfile);

            Conf.set("1", toolboxConfig.version);

            FileWorkerThreads.RUNCRC(toolboxConfig.DEFCRCfile);
        } else {
            FileWorkerThreads.RUNCRC(toolboxConfig.DEFCRCfile);
        }
    }
}
