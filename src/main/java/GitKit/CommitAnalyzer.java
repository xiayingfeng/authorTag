package GitKit;

import Entity.FileTag;
import Entity.LineTag;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Xia Yingfeng
 * @date 2021/12/13
 */
public class CommitAnalyzer extends AbstractCommitAnalyzer {
    private static final Logger logger = Logger.getLogger(CommitAnalyzer.class.getName());
    private static final String TIME_PAT_STR = "(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s[+-]\\d{4}\\s+)";
    private static final Pattern TIME_PATTERN = Pattern.compile(TIME_PAT_STR);


    /**
     * 通过对比两个Repo的首个commit的时间来判断parent与child关系
     *  @param left repo a
     * @param right repo b
     */
    @Override
    public boolean isParentRepo(Repository left, Repository right) {
        RevCommit leftHead = getFirstCommit(left);
        RevCommit rightHead = getFirstCommit(right);
        return isLeftEarlier(leftHead, rightHead);
    }

    /**
     * 获取一个Repo在特定commit后的文件集合
     * 标识文件的String为文件在项目中的全路径名
     * remark: git checkout | git reflog | git ls-files
     * 1. 先记录当前的HEAD ID
     * 2. git checkout到目标commit上
     * 3. git ls-files获得该commit下的文件名列表（可以考虑输出到文件中 >>，或直接利用）
     * 4.
     * @param repo 目标repo
     * @param commit 目标commit
     */
    @Override
    public HashSet<String> extractFilesSet(Repository repo, RevCommit commit) {
        HashSet<String> filesSet = null;
        // TODO use diffCommand to get differences
        RevCommit latestCommit = getLatestCommit(repo);

        return filesSet;
    }

    /**
     * 对比得到新增文件集合df
     *
     * @param base base点的文件集合
     * @param curr 最新一次Commit后的文件集合
     */
    @Override
    public HashSet<String> getDiffSet(HashSet<String> base, HashSet<String> curr) {
        // TODO
        return null;
    }

    /**
     * 获得两个Commit之间的Diff
     *
     * @param newCommit .
     * @param oldCommit .
     */
    @Override
    public List<DiffEntry> getDiffEntryList(Repository repo, RevCommit oldCommit, RevCommit newCommit) {
        logger.log(Level.INFO, "Compare 2 commits between ");
        List<DiffEntry> diffs = null;
        ObjectId newId = newCommit.getTree().getId();
        ObjectId oldId = oldCommit.getTree().getId();

        try (ObjectReader reader = repo.newObjectReader()){
            AbstractTreeIterator newTreeIter = prepareTreeParser(repo, String.valueOf(newCommit.getId()));

            AbstractTreeIterator oldTreeIter = prepareTreeParser(repo, String.valueOf(oldCommit.getId()));

            // finally, get the list of changed files
            try (Git git = new Git(repo)) {
                diffs= git.diff()
                        .setNewTree(newTreeIter)
                        .setOldTree(oldTreeIter)
                        .call();
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return diffs;
    }

    /** use RevWalk to quickly iterate over all available commits
     * From later to older*/
    @Override
    public List<RevCommit> getCommitList(Repository repo) {
        List<RevCommit> commitList = new ArrayList<>();
        try (Git git = new Git(repo)) {
            ObjectId branchId = repo.resolve("HEAD");
            Iterable<RevCommit> commits = git.log().add(branchId).call();
            for (RevCommit commit : commits) {
                commitList.add(commit);
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        logger.log(Level.FINE, "Get the commit list of repo " + repo.getIdentifier());
        return commitList;
    }

    /**
     * invoke getCommitList() to generate a commits set
     *
     * @param repo .
     */
    @Override
    public Set<RevCommit> getCommitSet(Repository repo) {
        List<RevCommit> commitList = getCommitList(repo);
        Set<RevCommit> commitSet = new HashSet<>(commitList);
        return commitSet;
    }

    /**
     * get the Blame-information for dir
     *
     * @param repo .
     * @param dirPath .
     */
    @Override
    public BlameResult getDirBlame(Repository repo, String dirPath) {
        return null;

    }

    /**
     * get the Blame-information for a file
     *  @param repo .
     *  @param filePath .
     */
    @Override
    public BlameResult getFileBlame(Repository repo, String filePath) throws GitAPIException {
        //TODO BlameCommand not work
        logger.log(Level.INFO, "Blaming " + filePath);
        BlameResult result = null;
//        result = new Git(repo).blame().setFilePath(filePath).call();
        try {
            BlameCommand blamer = new BlameCommand(repo)
                    .setFilePath(filePath)
                    .setStartCommit(repo.resolve("70eee48"))
                    .setTextComparator(RawTextComparator.WS_IGNORE_ALL);
            result = blamer.call();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get the Blame-info by invoke cmd in windows
     *
     * @param repoPath absolute path
     * @param filePath absolute path
     */
    @Override
    public FileTag getFileTagByCmd(String repoPath, String filePath) throws RuntimeException {
        String cmdStr = "cmd /c cd " + repoPath +
                " && " +
                "git blame -lw " + filePath;

        List<LineTag> lineTagList = new ArrayList<>();
        Set<String> shaSet = new HashSet<>();

        try {
            Runtime rt =Runtime.getRuntime();
            Process pr = rt.exec(cmdStr);

            BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                LineTag lineTag = lineToTag(line);
                lineTagList.add(lineTag);
                shaSet.add(lineTag.getSha());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FileTag(filePath, lineTagList, shaSet);
    }

    /** get sha of common commits  as a hashset*/
    public Set<String> getCommonCommitSet(Set<RevCommit> parentCommitSet, List<RevCommit> childCommitList) {
//        Set<RevCommit> parentCommitSet = getCommitSet(parent);
//        List<RevCommit> childCommitList = getCommitList(child);

        Set<String> parentShaSet = (HashSet)toStrCollection(parentCommitSet);
        List<String> childShaList = (ArrayList)toStrCollection(childCommitList);

        Set<String> commonSet = new HashSet<>();
        for (String sha : childShaList) {
            if (parentShaSet.contains(sha)) {
                commonSet.add(sha);
            }
        }
        return commonSet;
    }

    /** count how many lines in a child file are from parent */
    public int getParentCodeSumByFile(Repository parent, Repository child, String filePath) {
        Set<RevCommit> parentCommitSet = getCommitSet(parent);
        List<RevCommit> childCommitList = getCommitList(child);
        // repo level common commits set
        Set<String> commonSha = getCommonCommitSet(parentCommitSet, childCommitList);

        String repoDir = child.getDirectory().getParent();
        int count = getParentCodeSumByFile(commonSha, getFileTagByCmd(repoDir, filePath));
        return count;
    }

    /** common codes in by file and by repo, to count parent lines sum in a single file*/
    private int getParentCodeSumByFile(Set<String> commonSha, FileTag fileTag) {
//        FileTag fileTag= getFileTagByCmd(childRepoDir, filePath);
        List<LineTag> lineTags = fileTag.getLines();
        int count = 0;
        for (LineTag line : lineTags) {
            String tmpSha = line.getSha();
            if (commonSha.contains(tmpSha)) {
                count++;
            }
        }
        return count;
    }


    public int getParentCodeSumByRepo(Repository parent, Repository child) {
        int total = 0, parentCount = 0;

        // TODO may be this should be extracted.
        Set<RevCommit> parentCommitSet = getCommitSet(parent);
        List<RevCommit> childCommitList = getCommitList(child);
        // repo level common commits set
        Set<String> commonSha = getCommonCommitSet(parentCommitSet, childCommitList);

        String childRepoDir = child.getDirectory().getParent();
        File dir = new File(childRepoDir);
        if (!dir.isDirectory()) {
            throw new RuntimeException(childRepoDir + " is not a directory");
        }

        //TODO .git directory required to be remove from list
        Vector<File> fileVector = new Vector<>();
        fileVector.add(dir);
        while(!fileVector.isEmpty()) {
            File currFile = fileVector.firstElement();
            if (currFile.isFile()) {
                String filePath = currFile.getAbsolutePath();
                FileTag fileTag;
                try {
                     fileTag = getFileTagByCmd(childRepoDir, filePath);
                } catch (RuntimeException e) {
                    // while line analyze failed, log it
                    logger.log(Level.SEVERE, filePath + " read failed.");
                    fileVector.remove(currFile);
                    continue;
                }
                total += fileTag.getSize();
                parentCount += getParentCodeSumByFile(commonSha, fileTag);
                logger.log(Level.INFO, "total: " + total +
                        "\tparent count: " + parentCount + "\tfile: " + filePath);
            }
            else if (currFile.isDirectory()) {
                File[] children = currFile.listFiles();
                if (children != null) {
                    fileVector.addAll(Arrays.asList(children));
                }
            }
            fileVector.remove(currFile);
        }
        logger.log(Level.INFO,
                "repo: " + childRepoDir +
                        "\ntotal: " + total +
                        "\nparent count: " + parentCount +
                        "\nparent percentage: " + parentCount / total);
        return 0;
    }

    /** extract the information in single line, and transfer to LineTag */
    private LineTag lineToTag(String line) throws RuntimeException{
        // split line to 2 part
        String[] parts = line.split(TIME_PAT_STR);

        // find the sha, before ' ' and replace all '^'
        int rightOfSha = parts[0].indexOf(' ');
        String sha = parts[0].substring(0, rightOfSha).replace("^", "");

        // find the author, after '('
        int leftOfAuthor = parts[0].indexOf('(') + 1;
        String author = parts[0].substring(leftOfAuthor).trim();

        // get time stamp
        Matcher matcher = TIME_PATTERN.matcher(line);
        String timeStamp;
        if (matcher.find()) {
            timeStamp = matcher.group().substring(0, 19);
        }
        //TODO handle not match
        else {
            throw new RuntimeException("Date extract Failed: " + line);
        }

        // get line number
        int posBracket = parts[1].indexOf(')');
        int lineNo = Integer.parseInt(parts[1].substring(0, posBracket));
        String content = parts[1].substring(posBracket + 1);

        return new LineTag(lineNo, content, sha, author, timeStamp);
    }

    /** 获取特定Repository的首个Commit*/
    private RevCommit getFirstCommit(Repository repo) {
        List<RevCommit> commits = getCommitList(repo);
        int size = commits.size();
        return commits.get(size - 1);
    }

    /**
     * 获取特定Repository的最新Commit*/
    private RevCommit getLatestCommit(Repository repo) {
        return getCommitList(repo).get(0);
    }

    /**
     * StatusCommand获取指定Repo当前的Status*/
    private Status getStatus(Repository repo) {
        StatusCommand statusCommand = new StatusCommand(repo){};
        Status status = null;
        try {
            status = statusCommand.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 比较左边的commit是否比右边的commit更早*/
    private boolean isLeftEarlier(RevCommit aCommit, RevCommit bCommit) {
        Date aDate = aCommit.getAuthorIdent().getWhen();
        Date bDate = bCommit.getAuthorIdent().getWhen();
        return aDate.before(bDate);
    }

    /**
     * 获取特定branch的commit
     * @author Kaifeng Huang
     * @source JGitCommand
     * modified by Xia Yingfeng, at 2021/12/15
     * */
    private RevCommit revCommitOfBranchRef(Ref branch, RevWalk revWalk) {
        RevCommit commit = null;
        try {
            RevObject object = revWalk.parseAny(branch.getObjectId());
            if (object instanceof RevCommit) {
                commit = (RevCommit) object;
            } else {
                System.err.println("invalid");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commit;
    }

    /**
     * checkout to one commit with specific commit id
     *
     * @param repo target repository
     * @param commitid target commit id
     * @author Kaifeng Huang
     * @source JGitCommand
     * modified by Xia Yingfeng, at 2021/12/16
     * */
    private boolean checkout(Repository repo, String commitid) {
        boolean status = false;
        Git git = new Git(repo);
        try {
            CheckoutCommand checkoutCommand = git.checkout();
            checkoutCommand
                    .setName(commitid)
                    .call();
            logger.log(Level.FINE, "Checkout to " + commitid);
            status =true;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return status;
    }

    /** extract the sha info of Commit Collection, and return corresponding collection */
    private Collection<String> toStrCollection(Collection<RevCommit> commits){
        Collection<String> strCollection = null;
        try {
            strCollection = commits.getClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        for (RevCommit commit : commits) {
            if (strCollection != null) {
                strCollection.add(commit.getName());
            } else {
                throw new RuntimeException("strCollection is null");
            }
        }
        return strCollection;
    }
}
