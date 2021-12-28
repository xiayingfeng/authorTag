package GitKit;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Xia Yingfeng
 * @date 2021/12/20
 */
class CommitAnalyzerTest {
    private static final Logger logger = Logger.getLogger(CommitAnalyzerTest.class.getName());
    private static Repository child, parent;
    private static CommitAnalyzer commitAnalyzer;

    static {
        commitAnalyzer = new CommitAnalyzer();
        try {
            child = new RepositoryBuilder()
                    .setGitDir(new File("F:/SelfFileBackUp/Term/Lab/License_Reading/authorTag/src/main/resources/repos/google__fdse__dagger/dagger/.git"))
                    .build();
            parent = new RepositoryBuilder()
                    .setGitDir(new File("F:/SelfFileBackUp/Term/Lab/License_Reading/authorTag/src/main/resources/repos/square__fdse__dagger/dagger/.git"))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    CommitAnalyzerTest() throws IOException {
    }

    @Test
    void isParentRepo() {
    }

    @Test
    void extractFilesSet() throws IOException {

        String firstDiffId = "1f4351707";
        RevWalk walk = new RevWalk(child);
        RevCommit commit = walk.parseCommit(child.resolve(firstDiffId));
        HashSet<String> filesSet = new CommitAnalyzer().extractFilesSet(child, commit);
        Assertions.assertIterableEquals(filesSet, getSetFromTxt("src/main/resources/repos/1f4351707.txt"));
    }

    @Test
    void getDiffSet() {
    }

    @Test
    void getDiffEntryList() throws IOException {
        Repository repo = new RepositoryBuilder()
                .setGitDir(new File("src/main/resources/repos/google__fdse__dagger/dagger/.git"))
                .build();
        String oldIdStr = "1f4351707"; //base
//        String oldIdStr = "63f1feb88";
        ObjectId oldId = repo.resolve(oldIdStr).toObjectId();
        String newIdStr = "c3f244947";
        ObjectId newId = repo.resolve(newIdStr).toObjectId();
        RevWalk revWalk = new RevWalk(repo);
        RevCommit oldCommit = revWalk.parseCommit(oldId);
        RevCommit newCommit = revWalk.parseCommit(newId);
        List<DiffEntry> diffEntries = new CommitAnalyzer().getDiffEntryList(repo, oldCommit, newCommit);
        System.out.println(diffEntries);

    }


    @Test
    void getCommitList() throws IOException {
//        Repository repo = new RepositoryBuilder()
//                .setGitDir(new File("src/main/resources/repos/google__fdse__dagger/dagger/.git"))
//                .build();
//        List<RevCommit> commits = new CommitAnalyzer().getCommitList(repo);
//        System.out.println("pause here");
    }



    private HashSet<String> getSetFromTxt(String filePath) {
        HashSet<String> filesSet = new HashSet<>();
        File file = new File(filePath);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            for (String tmp = bufferedReader.readLine(); tmp != null; tmp = bufferedReader.readLine()){
                filesSet.add(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filesSet;
    }

    @Test
    void getDirBlame() {
    }

    @Test
    void getFileBlame() {
        String filePath = "F:/SelfFileBackUp/Term/Lab/License_Reading/authorTag/src/main/resources/repos/google__fdse__dagger/dagger/README.md";
//        String filePath = "F:/SelfFileBackUp/Term/Lab/License_Reading/authorTag/src/main/java/GitKit/CommitAnalyzer.java";
        Assertions.assertTrue(new File(filePath).exists(), "File exists.");
        BlameResult result = null;
        try {
            result = new CommitAnalyzer().getFileBlame(child, filePath);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        Assertions.assertNotNull(result, "result should not be null.");
    }

    @Test
    void getFileBlameByCmd() {
        logger.log(Level.INFO, "Test getFileBlameByCmd()");
        String repoPath = "F:/SelfFileBackUp/Term/Lab/License_Reading/authorTag/src/main/resources/repos/google__fdse__dagger/dagger";
        String filePath = "F:/SelfFileBackUp/Term/Lab/License_Reading/authorTag/src/main/resources/repos/google__fdse__dagger/dagger/README.md";
        commitAnalyzer.getFileTagByCmd(repoPath, filePath);
    }

    @Test
    void getCommonCommitSet() {
        Set<String> commonCommitSet = commitAnalyzer.getCommonCommitSet(parent, child);
        logger.log(Level.INFO, "Test getCommonCommitSet()");

    }
}