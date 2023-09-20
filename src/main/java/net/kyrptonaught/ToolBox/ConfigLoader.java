package net.kyrptonaught.ToolBox;

import com.google.gson.*;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

public class ConfigLoader {
    public static Gson gson = new Gson().newBuilder()
            .setLenient()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public static BranchesConfig parseBranches(String json) {
        return gson.fromJson(json, BranchesConfig.class);
    }

    public static BranchConfig parseToolboxConfig(String json) {
        return gson.fromJson(json, BranchConfig.class);
    }

    public static InstalledServerInfo parseToolboxInstall(String json) {
        return gson.fromJson(json, InstalledServerInfo.class);
    }

    public static String serializeToolboxInstall(Object config) {
        return gson.toJson(config);
    }
}