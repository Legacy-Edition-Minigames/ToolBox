package net.kyrptonaught.ToolBox.IO;

public class GithubHelper {
    public static String convertRepoToZipball(String repo) {
        return repo.replace("/tree/", "/archive/refs/heads/") + ".zip";
    }

    public static String convertRepoToToolboxConfig(String repo) {
        return repo.replace("github.com", "raw.githubusercontent.com").replace("tree/", "") + "/.toolbox/toolbox.json";
    }

    public static String convertRepoToApiCall(String repo) {
        return repo.replace("github.com", "api.github.com/repos").replace("/tree/", "/branches/");
    }
}
