package net.kyrptonaught.ToolBox.configs;

import java.util.List;

public class BranchesConfig {
    public List<BranchInfo> branches;

    public static class BranchInfo {
        public String name;
        public String url;
        public String desc;
    }
}
