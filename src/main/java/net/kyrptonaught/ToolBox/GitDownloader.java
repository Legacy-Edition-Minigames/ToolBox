package net.kyrptonaught.ToolBox;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.merge.ContentMergeStrategy;
import org.eclipse.jgit.merge.MergeStrategy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitDownloader {

    public static void cloneBranch(String git, String path) {
        gitClone(git, path);
    }

    public static void gitClone(String repo, String path) {
        Path out = Path.of(path);
        if (!Files.exists(out)) {
            try (Git git = Git.cloneRepository()
                    .setURI(repo)
                    .setDirectory(out.toFile())
                    .setBranch("toolbox-testing")
                    .setRemote("toolbox-testing")
                    .setProgressMonitor(new TextProgressMonitor())
                    .call()) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try (Git git = Git.open(new File(path))) {
            git.reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .setProgressMonitor(new TextProgressMonitor())
                    .call();

            git
                    .pull()
                    .setStrategy(MergeStrategy.THEIRS)
                    .setContentMergeStrategy(ContentMergeStrategy.THEIRS)
                    .setRemoteBranchName("toolbox-testing")
                    .setProgressMonitor(new TextProgressMonitor())
                    .call();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


