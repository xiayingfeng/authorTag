package descrscanner;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import constant.Constant;
import descrscanner.format.Patterns;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import utils.RepoLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Xia Yingfeng
 * @date 2022/3/28
 */
public class DescrScanner implements IDescrScanner {
    private static final Logger logger = Logger.getLogger(DescrScanner.class.getName());
    private static final RepoLoader REPO_READER = RepoLoader.getRepoReader();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Repository repo;
    private Git git;
    //

    /**
     * get list of file containing modification description of specified repository
     *
     * @param repoName repo name with owner eg: google/dagger
     * @return file list
     */
    @Override
    public List<File> getDescrFileList(String repoName) {
        repo = REPO_READER.readRepo(repoName);
        git = Git.wrap(repo);
        File dir = repo.getDirectory().getParentFile();
        return bfsSearch(dir);
    }

    /**
     * get list of Description of specified repository
     *
     * @param repoName repo name with owner eg: google/dagger
     * @return description blocks list
     */
    @Override
    public List<DescriptionFile> getDescrList(String repoName) throws GitAPIException, IOException {
        List<DescriptionFile> fileDescrList = new ArrayList<>();

        List<File> descrFiles = getDescrFileList(repoName);
        for (File file : descrFiles) {
            List<Description> descrList = extractDescr(file);
            DescriptionFile fileDescr = new DescriptionFile(repoName, descrList);
            fileDescrList.add(fileDescr);
        }
        return fileDescrList;
    }

    /**
     * get list of hunks of target repo after a specified commit
     *
     * @param startCommit left start commit
     * @return hunk headers list
     * @throws GitAPIException might be thrown during LogCommand been called
     */
    @Override
    public List<HunkHeader> getHunks(RevCommit startCommit) throws GitAPIException, IOException {
        // in reverse chronological order
        Iterable<RevCommit> commits = git.log().call();

        // now only start commit and the latest commit
        RevCommit currCommit = commits.iterator().next();
        List<DiffEntry> diffList = getDiffEntry(startCommit, currCommit);
        return getHunkFromDiffEntry(diffList);
    }

    public List<DescrHunkPair> getDescrHunkPairs(List<DescriptionFile> fileDescrList) throws GitAPIException, IOException {
        for (DescriptionFile fileDescr : fileDescrList) {
            List<Description> descrList = fileDescr.getDescrList();
            for (Description description : descrList) {
                Date leftEnd = description.getLeftEnd();
                Date rightEnd = description.getRightEnd();
                TreeMap<Date, RevCommit> commitsByDate = getAllCommitsBetween(leftEnd, rightEnd);
                RevCommit leftCommit = commitsByDate.firstEntry().getValue();
                RevCommit rightCommit = commitsByDate.lastEntry().getValue();
                List<DiffEntry> diffEntries = getDiffEntry(leftCommit, rightCommit);
                List<HunkHeader> hunkHeaders = getHunkFromDiffEntry(diffEntries);
                break;
            }
        }
        return null;
    }

    /*
     * Get all commits before rightEnd and after leftEnd
     * */
    private TreeMap<Date, RevCommit> getAllCommitsBetween(Date leftEnd, Date rightEnd) {
        RevWalk walk = new RevWalk(this.repo);
        TreeMap<Date, RevCommit> commitsByDate = new TreeMap<>();
        try {
            walk.markStart(walk.parseCommit(this.repo.resolve(Constants.HEAD)));

            walk.sort(RevSort.COMMIT_TIME_DESC);
//            walk.setTreeFilter(PathFilter.create(path));

            for (RevCommit commit : walk) {
                Date commitTime = commit.getCommitterIdent().getWhen();
                boolean isAfterLeft = commitTime.after(leftEnd);
                boolean isBeforeRight = commitTime.before(rightEnd);
                if (isAfterLeft && isBeforeRight) {
                    commitsByDate.put(commitTime, commit);
                }
            }
            walk.close();
            logger.log(Level.INFO, "Number of valid commits: " + commitsByDate.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commitsByDate;
    }


    /**
     * extract hunks from diff entry list
     *
     * @return a list contains all hunks in list of diff entries
     * @throws IOException DiffEntry could not be read during transmute entry to FileHeader.
     */
    private List<HunkHeader> getHunkFromDiffEntry(List<DiffEntry> diffList) throws IOException {
        List<HunkHeader> headerList = new ArrayList<>();

        // format diff
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter formatter = new DiffFormatter(out);
        // ignore all white space
        formatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        formatter.setRepository(repo);
        for (DiffEntry entry : diffList) {
            FileHeader fileHeader = formatter.toFileHeader(entry);

            // Not sure if the cast is correct
            List<HunkHeader> tempList = (List<HunkHeader>) fileHeader.getHunks();
            headerList.addAll(tempList);
        }
        return headerList;
    }

    /**
     * get diff entry list of 2 adjacent commit version.
     * Is it possible to compare any two version?
     *
     * @param currCommit current commit
     * @param prevCommit previous commit
     * @return a list of DiffEntries between prevCommit and currCommit
     * @throws IOException     throwable during reset TreeParser
     * @throws GitAPIException throwable during calling diff command
     */
    private List<DiffEntry> getDiffEntry(RevCommit prevCommit, RevCommit currCommit) throws IOException, GitAPIException {
        List<DiffEntry> diffList;
        ObjectId currHead = currCommit.getTree().getId();
        ObjectId prevHead = prevCommit.getTree().getId();
        logger.log(Level.INFO, "Comparing " + prevHead + "to" + currHead);

        ObjectReader reader = repo.newObjectReader();
        CanonicalTreeParser prevParser = new CanonicalTreeParser();
        prevParser.reset(reader, prevHead);
        CanonicalTreeParser currParser = new CanonicalTreeParser();
        currParser.reset(reader, currHead);


        diffList = git.diff()
                .setOldTree(prevParser)
                .setNewTree(currParser)
                .setOutputStream(System.out)
//                .setContextLines()
                .call();

        return diffList;
    }

    /**
     * @param dir the root level dir, same level as .gitignore
     * @return list of files whose name contains keywords
     */
    private List<File> bfsSearch(File dir) {
        List<File> destFileList = new ArrayList<>();
        File[] firstLevelFiles = dir.listFiles();
        if (firstLevelFiles == null) {
            return destFileList;
        }

        Vector<File> fileVector = new Vector<>();
        // add first level unhidden files(file name not begins with '.') into vector
        for (File file : firstLevelFiles) {
            boolean isHidden = (file.getName().charAt(0) == '.');
            if (isHidden) {
                continue;
            }
            fileVector.add(file);
        }

        // search files whose name contains keywords by bfs
        while (!fileVector.isEmpty()) {
            File currFile = fileVector.firstElement();
            if (currFile.isDirectory()) {
                File[] children = currFile.listFiles();
                if (children != null) {
                    fileVector.addAll(Arrays.asList(children));
                }
            } else if (hasKeywords(currFile.getName())) {
                destFileList.add(currFile);
            }
            fileVector.removeElement(currFile);
        }

        return destFileList;
    }

    /**
     * compare to KEY_WORDS array, verify filename contains keys or not
     *
     * @param fileName name of file to be detected
     * @return describing keywords contained in filename or not
     */
    private boolean hasKeywords(String fileName) {
        boolean hasKey = false;
        for (int i = 0; i < Constant.KEY_WORDS.length && !hasKey; i++) {
            hasKey = fileName.toUpperCase().contains(Constant.KEY_WORDS[i]);
        }
        return hasKey;
    }

    /**
     * extract description list from single file
     */
    private List<Description> extractDescr(File file) throws IOException, GitAPIException {
        List<Description> descrList = new ArrayList<>();
        List<String> strList = Files.readLines(file, Charsets.UTF_8);

        //TODO find patterns at first
        Patterns patterns = null;

        List<String> descrBlock = new ArrayList<>();
//        boolean isInitiated = false;
        Date tmpDate = null;
        Date leftDate = null, rightDate = null;

        int size = strList.size();
        int i = 0;
        // we first find the date and version pattern of this file
        for (; i < size; i++) {
            String currLine = strList.get(i);
            if (Patterns.isFirstPatternStr(currLine)) {
                patterns = new Patterns(currLine);
                tmpDate = extractDate(currLine, patterns);
                break;
            }
        }

        // while this file not contain a Patterns
        if (patterns == null) {
            String filePath = file.getAbsolutePath();
            logger.log(Level.INFO, "No patterns found in " + filePath);
            Description description = wholeAsSingleBlock(strList);
            descrList.add(description);
            return descrList;
        }

        // each time a break line is found, intercept a changelog block
        for (i = i + 1; i < size; i++) {
            String currLine = strList.get(i);
            if (patterns.isComplyWithPatterns(currLine)) {
                rightDate = tmpDate;
                tmpDate = extractDate(currLine, patterns);
                leftDate = tmpDate;
                Description currDescr = new Description(leftDate, rightDate, descrBlock);
                descrList.add(currDescr);
                descrBlock = new ArrayList<>();
            } else {
                descrBlock.add(currLine);
            }
        }

        //TODO finally, close the last changelog block
        return descrList;
    }

    private Description wholeAsSingleBlock(List<String> strList) throws GitAPIException {
        StringBuilder builder = new StringBuilder();
        for (String line : strList) {
            builder.append(line);
        }
        String block = builder.toString();
        ArrayList<String> descrBlockList = new ArrayList<>(1);
        descrBlockList.add(block);
        // as for single whole unformatted file, set leftEnd as 0, and rightEnd as latest commit date
        // 干脆对于非格式化文件，只设置结束时间为repo的最后一次提交时间，将开始时间置空

        // in reverse chronological order
        Iterable<RevCommit> commits = git.log().call();

        // now only start commit and the latest commit
        RevCommit latestCommit = commits.iterator().next();

        Date rightEnd = new Date(latestCommit.getCommitTime());
        return new Description(rightEnd, descrBlockList);
    }

    private Date extractDate(String line, Patterns patterns) {
        String dateStr = patterns.extractDate(line);

        Date dateTime = null;
        try {
            dateTime = SIMPLE_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }
}
