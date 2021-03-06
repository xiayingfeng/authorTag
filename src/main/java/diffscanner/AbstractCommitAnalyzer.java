package diffscanner;

import entity.FileTag;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Xia Yingfeng
 * @date 2021/12/13
 */
public abstract class AbstractCommitAnalyzer {

    /**
     * 通过对比两个Repo的首个commit的时间来判断parent与child关系
     *
     * @return describing the left is the forked repo or not
     */
    abstract boolean isLeftParentRepo(Repository left, Repository right);

    /**
     * 获取一个Repo在特定commit后的文件集合
     * 标识文件的String为文件在项目中的全路径名
     *
     * @param repo   object repository
     * @param commit base commit
     * @return absolute path of all files
     */
    abstract HashSet<String> extractFilesSet(Repository repo, RevCommit commit);

    /**
     * 对比得到新增文件集合df
    * */
    abstract HashSet<String> getDiffSet(HashSet<String> base, HashSet<String> curr);

    /**
     * 对比得到一个Repo的两个Commit之间的文件差异*/
    abstract List<DiffEntry> getDiffEntryList(Repository repo, RevCommit oldCommit, RevCommit newCommit);

    /** use RevWalk to quickly iterate over all available commits list*/
    abstract List<RevCommit> getCommitList(Repository repo);

    /**
     * invoke getCommitList() to generate a commits set
     */
    abstract Set<RevCommit> getCommitSet(Repository repo);

    /**
     * get the Blame-information for a dir
     */
    abstract BlameResult getDirBlame(Repository repo, String dirPath);

    /**
     * get the Blame-information for a file
     */
    abstract BlameResult getFileBlame(Repository repo, String filePath) throws GitAPIException;

    /**
     * get the Blame-info by invoke cmd in windows
     */
    public abstract FileTag getFileTagByCmd(String repoPath, String filePath) throws RuntimeException;

    /**
     * from the commit we can build the tree which allows us to construct the TreeParser
     */
    protected AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }


}
