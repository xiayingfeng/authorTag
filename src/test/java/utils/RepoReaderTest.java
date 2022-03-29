package utils;

import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.Test;

/**
 * @author Xia Yingfeng
 * @date 2022/3/30
 */
class RepoReaderTest {


    @Test
    void readRepo() {
        String repoName = "conventional-changelog/standard-version";
        Repository repository = RepoReader.readRepo(repoName);
        assert repository != null;
    }
}