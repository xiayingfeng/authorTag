package GitKit;

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

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Xia Yingfeng
 * @date 2021/12/20
 */
class CommitAnalyzerTest {

    @Test
    void isParentRepo() {
    }

    @Test
    void extractFilesSet() throws IOException {
        Repository repo = new RepositoryBuilder()
                .setGitDir(new File("src/main/resources/repos/google__fdse__dagger/dagger/.git"))
                .build();
        String firstDiffId = "1f4351707";
        RevWalk walk = new RevWalk(repo);
        RevCommit commit = walk.parseCommit(repo.resolve(firstDiffId));
        HashSet<String> filesSet = new CommitAnalyzer().extractFilesSet(repo, commit);
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
        Repository repo = new RepositoryBuilder()
                .setGitDir(new File("src/main/resources/repos/google__fdse__dagger/dagger/.git"))
                .build();
        List<RevCommit> commits = new CommitAnalyzer().getCommitList(repo);
        System.out.println("pause here");
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
}