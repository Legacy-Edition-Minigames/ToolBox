package net.kyrptonaught.ToolBox.configs;

public class BranchConfig {
    public String name;

    public String launchCMD;

    public Dependency[] dependencies;

    public static class Dependency {
        public String name;
        public String displayName;
        public String url;
        public String location;
        public boolean gitRepo = false;
        public boolean unzip = false;

        public String getDisplayName() {
            if (displayName == null) return name;
            return displayName;
        }
    }
}
