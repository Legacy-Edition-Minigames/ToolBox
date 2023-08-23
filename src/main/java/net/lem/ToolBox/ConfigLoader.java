package net.lem.ToolBox;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class ConfigLoader {
    public static Gson gson = new Gson().newBuilder()
            .setLenient()
            .serializeNulls()
            .create();


    public static BranchConfig parseBranches(String json) {
        return gson.fromJson(json, BranchConfig.class);
    }

    public static ToolboxConfig parseToolboxConfig(String json) {
        return gson.fromJson(json, ToolboxConfig.class);
    }


    public static class BranchConfig {
        HashMap<String, String> branches = new HashMap<>();
    }

    public static class ToolboxConfig {
        public String version;

        public String name;

        public String serverJarURL;

        public String LEMbaseURL;

        public String DEFCRCfile;

        public HashMap<String, String> mods = new HashMap<>();

    }
}
