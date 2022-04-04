package diffscanner;

import entity.FileTag;
import entity.LineTag;
import entity.RepoTag;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.ShellCaller.call;

/**
 * @author Xia Yingfeng
 * @date 2021/12/13
 */
public class CommitAnalyzer extends AbstractCommitAnalyzer {
    private static final Logger logger = Logger.getLogger(CommitAnalyzer.class.getName());

    private static final String TIME_PAT_STR = "(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s[+-]\\d{4}\\s+)";
    private static final Pattern TIME_PATTERN = Pattern.compile(TIME_PAT_STR);


    /**
     * judge which repo is forking and which is forked by contrasting date of first commits of 2 repositories
     *
     * @param left  repo a
     * @param right repo b
     */
    @Override
    public boolean isLeftParentRepo(Repository left, Repository right) {
        RevCommit leftHead = getFirstCommit(left);
        RevCommit rightHead = getFirstCommit(right);
        return isLeftEarlier(leftHead, rightHead);
    }

    /**
     * get files set of the specified repository after some specified commit
     * the context of String, which is used to identify each files, is the full path of the file in the project
     * remark: git checkout | git reflog | git ls-files
     * 1. record the current HEAD ID
     * 2. 'git checkout' to target commit
     * 3. 'git ls-files' to get list of names of files changed by that commit
     * (can we just output it to log file, or use '>>' ?)
     * @param repo target repo
     * @param commit target commit
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
     * get differences of 2 specified Commits
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
            // use gitLog Command to traverse the commit tree
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
        return new HashSet<>(commitList);
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
        try {
            BlameCommand blamer = new BlameCommand(repo)
                    .setFilePath(filePath)
                    .setStartCommit(repo.resolve("70eee48"))
                    //TODO hard-code should be fixed
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
    public FileTag getFileTagByCmd(String repoPath, String filePath, String platform) throws RuntimeException {

        String cmdStr = "";

        cmdStr += "git  " +
                "--git-dir=" + repoPath + "/.git " +
                "--work-tree=" + repoPath +
                " blame -lw " + filePath;

        List<LineTag> lineTagList = new ArrayList<>();
        Set<String> shaSet = new HashSet<>();

        List<String> linesList = call(cmdStr);
        for (String line : linesList) {
                LineTag lineTag = lineToTag(line);
                lineTagList.add(lineTag);
                shaSet.add(lineTag.getSha());
        }
        return new FileTag(filePath, lineTagList, shaSet);
    }

    /** get sha of common commits  as a hashset*/
    public Set<String> getCommonCommitSet(Set<RevCommit> parentCommitSet, List<RevCommit> childCommitList) {
        Set<String> parentShaSet = (HashSet<String>)toStrCollection(parentCommitSet);
        List<String> childShaList = (ArrayList<String>)toStrCollection(childCommitList);

        Set<String> commonSet = new HashSet<>();
        for (String sha : childShaList) {
            if (parentShaSet.contains(sha)) {
                commonSet.add(sha);
            }
        }
        return commonSet;
    }

    /** count how many lines in a child file are from parent */
    public int getParentCodeSumByFile(Repository parent, Repository child, String filePath, String platform) {
        Set<RevCommit> parentCommitSet = getCommitSet(parent);
        List<RevCommit> childCommitList = getCommitList(child);
        // repo level common commits set
        Set<String> commonSha = getCommonCommitSet(parentCommitSet, childCommitList);

        String repoDir = child.getDirectory().getParent();
        int count = getParentCodeSumByFile(commonSha, getFileTagByCmd(repoDir, filePath, platform));
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

    /** get the total lines derived from parent repo in child repo */
    public RepoTag getParentCodeSumByRepo(Repository parent, Repository child, String platform) {
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

        // remove hidden directories like .git from root File list
        // traverse the first level elements of dir, and filter out directories whose names starting with '.'
        Vector<File> fileVector = new Vector<>();
        File[] elementsArray = dir.listFiles();
        if (null != elementsArray) {
            for (File tempFile : elementsArray) {
                if (tempFile.getName().charAt(0) != '.') {
                    fileVector.add(tempFile);
                }
            }
        }

        while(!fileVector.isEmpty()) {
            File currFile = fileVector.firstElement();
            if (currFile.isFile()) {
                String filePath = currFile.getAbsolutePath();
                FileTag fileTag;
                try {
                     fileTag = getFileTagByCmd(childRepoDir, filePath, platform);
                } catch (RuntimeException e) {
                    // while line analyze failed, log it
                    logger.log(Level.INFO , filePath + " read failed.");
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

        return new RepoTag(parent.getDirectory().getParent(), childRepoDir, total, parentCount);
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
            throw new RuntimeException("Date extract Failed:  " + line);
        }

        // get line number
        int posBracket = parts[1].indexOf(')');
        int lineNo = Integer.parseInt(parts[1].substring(0, posBracket));
        String content = parts[1].substring(posBracket + 1);

        return new LineTag(lineNo, content, sha, author, timeStamp);
    }

    /** Get the first commit of the specified repository*/
    private RevCommit getFirstCommit(Repository repo) {
        List<RevCommit> commits = getCommitList(repo);
        int size = commits.size();
        return commits.get(size - 1);
    }

    /**
     * Get the latest commit of the specified repository*/
    private RevCommit getLatestCommit(Repository repo) {
        return getCommitList(repo).get(0);
    }

    /**
     * Get the current status of the specified repository through StatusCommand */
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
     * is the left commit earlier than the right
     */
    private boolean isLeftEarlier(RevCommit aCommit, RevCommit bCommit) {
        Date aDate = aCommit.getAuthorIdent().getWhen();
        Date bDate = bCommit.getAuthorIdent().getWhen();
        return aDate.before(bDate);
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
