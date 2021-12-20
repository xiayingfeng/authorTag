package GitKit;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.HashSet;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2021/12/13
 */
public interface ICommitAnalyzer {

    /**
     * 通过对比两个Repo的首个commit的时间来判断parent与child关系
    *
     * @param left
     * @param right*/
    public boolean isParentRepo(Repository left, Repository right);

    /**
     * 获取一个Repo在特定commit后的文件集合
     * 标识文件的String为文件在项目中的全路径名
     */
    public HashSet<String> extractFilesSet(Repository repo, RevCommit commit);

    /**
     * 对比得到新增文件集合df
    * */
    public HashSet<String> getDiffSet(HashSet<String> base, HashSet<String> curr);

    /**
     * 对比得到一个Repo的两个Commit之间的文件差异*/
    public List<DiffEntry> getDiffEntryList(Repository repo, RevCommit oldCommit, RevCommit newCommit);

    /** use RevWalk to quickly iterate over all available commits*/
    public List<RevCommit> getCommitList(Repository repo);




}
