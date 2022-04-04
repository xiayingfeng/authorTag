package descrscanner;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/3/28
 */
public interface IDescrScanner {
    /**
     * get list of file containing modification description of specified repository
     *
     * @param repoName repo name with owner eg: google/dagger
     * @return file list
     */
    List<File> getDescrFileList(String repoName);

    /**
     * get list of Description of specified repository
     *
     * @param repoName repo name with owner eg: google/dagger
     * @return description blocks list
     */
    List<Description> getDescrList(String repoName);

    /**
     * get list of hunks of target repo after a specified commit
     *
     * @param repo        research object repository
     * @param startCommit left start commit
     * @return hunk headers list
     * @throws GitAPIException might be thrown during LogCommand been called
     * @throws IOException     throwable during reset TreeParser
     */
    List<HunkHeader> getHunks(Repository repo, RevCommit startCommit) throws GitAPIException, IOException;

}
