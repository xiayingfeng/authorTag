package descrscanner;

import constant.Constant;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import utils.RepoLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Xia Yingfeng
 * @date 2022/3/28
 */
public class DescrScanner implements IDescrScanner {
    public static final Logger logger = Logger.getLogger(DescrScanner.class.getName());
    RepoLoader repoLoader = RepoLoader.getRepoReader();

    /**
     * get list of file containing modification description of specified repository
     *
     * @param repoName repo name with owner eg: google/dagger
     * @return file list
     */
    @Override
    public List<File> getDescrFileList(String repoName) {
        Repository repo = repoLoader.readRepo(repoName);
        File dir = repo.getDirectory();
        return bfsSearch(dir);
    }

    /**
     * get list of Description of specified repository
     *
     * @param repoName repo name with owner eg: google/dagger
     * @return description blocks list
     */
    @Override
    public List<Description> getDescrList(String repoName) {
        List<File> descrFiles = getDescrFileList(repoName);
        //TODO extract description blocks
        return null;
    }

    /**
     * get list of hunks of target repo after a specified commit
     *
     * @param repo        research object repository
     * @param startCommit left start commit
     * @return hunk headers list
     * @throws GitAPIException might be thrown during LogCommand been called
     */
    @Override
    public List<HunkHeader> getHunks(Repository repo, RevCommit startCommit) throws GitAPIException, IOException {
        Git git = Git.wrap(repo);

        // in reverse chronological order
        Iterable<RevCommit> commits = git.log().call();

        // now only start commit and the latest commit
        RevCommit currCommit = commits.iterator().next();
        List<DiffEntry> diffList = getDiffEntry(git, repo, startCommit, currCommit);
        return getHunkFromDiffEntry(repo, diffList);
    }

    /**
     * extract hunks from diff entry list
     * @throws IOException DiffEntry could not be read during transmute entry to FileHeader.
     * @return a list contains all hunks in list of diff entries */
    private List<HunkHeader> getHunkFromDiffEntry (Repository repo, List<DiffEntry> diffList) throws IOException {
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
     * @param git        Git object
     * @param repo       research object repository
     * @param currCommit current commit
     * @param prevCommit previous commit
     * @return a list of DiffEntries between prevCommit and currCommit
     * @throws IOException     throwable during reset TreeParser
     * @throws GitAPIException throwable during calling diff command
     */
    private List<DiffEntry> getDiffEntry(Git git, Repository repo, RevCommit prevCommit, RevCommit currCommit) throws IOException, GitAPIException {
        List<DiffEntry> diffList;
        ObjectId currHead = currCommit.getTree().getId();
        ObjectId prevHead = prevCommit.getTree().getId();
        logger.log(Level.INFO, "Comparing " + prevHead + "to" + currHead);

        ObjectReader reader = repo.newObjectReader();
        CanonicalTreeParser prevParser = new CanonicalTreeParser();
        prevParser.reset(reader, prevCommit);
        CanonicalTreeParser currParser = new CanonicalTreeParser();
        currParser.reset(reader, currCommit);

        diffList = git.diff().setOldTree(prevParser).setNewTree(currParser).call();

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
        for (File file : fileVector) {
            if (file.isDirectory()) {
                fileVector.add(file);
            } else if (hasKeywords(file.getName())) {
                destFileList.add(file);
            }
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
}
