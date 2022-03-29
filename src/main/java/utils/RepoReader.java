package utils;

import constant.Constant;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Xia Yingfeng
 * @date 2022/3/28
 */

public class RepoReader {
    private static final Logger logger = Logger.getLogger(RepoReader.class.getName());
    private static final FileRepositoryBuilder BUILDER = new FileRepositoryBuilder();

    public static Repository readRepo(String repoName) {
        String absRepoPath = getRepoPath(repoName);
        File gitFile = null;
        try {
            gitFile = new File(absRepoPath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot find \"" + absRepoPath + "\"");
        }

        Repository repository = null;
        try {
            repository = BUILDER.setGitDir(gitFile)
                    .readEnvironment()
                    .findGitDir()
                    .build();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to build repository \"" + absRepoPath + "\"");
        }
        return repository;
    }

    /**
     * @param repoName repoOwner/repoName eg: google/dagger
     * @return absolute file path adapted to platform
     * eg: Constant.REPOS_DIR + google__fdse__dagger/dagger
     */
    public static String getRepoPath(String repoName) {
        String localRepoName = repoName.replace("/", "__fdse__");
        // the name of repo without owner info, eg: dagger
        String realRepoName = repoName.split("/")[1];
        String path = Constant.REPOS_DIR;
        if (Constant.PLATFORM.equals(Constant.WIN)) {
            path += "//" + localRepoName + "//" + realRepoName;
        } else if (Constant.PLATFORM.equals(Constant.LIN)) {
            path += "/" + localRepoName + "/" + realRepoName;
        }
        logger.log(Level.INFO, "absRepoPath: " + path);
        return path;
    }
}
