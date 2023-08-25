package net.kyrptonaught.ToolBox;

import com.google.gson.Gson;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

public class ConfigLoader {
    public static Gson gson = new Gson().newBuilder()
            .setLenient()
            .serializeNulls()
            .create();


    public static BranchesConfig parseBranches(String json) {
        return gson.fromJson(json, BranchesConfig.class);
    }

    public static BranchConfig parseToolboxConfig(String json) {
        return gson.fromJson(json, BranchConfig.class);
    }
}
