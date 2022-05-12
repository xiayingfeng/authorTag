package descrscanner.entities;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/4/13
 */
public class DescrHunkPair {
    private final Description descr;
    private final String hunksContent;
    private List<RevCommit> commitList;

    public DescrHunkPair(Description descr, String hunksContent, List<RevCommit> commitList) {
        this.descr = descr;
        this.hunksContent = hunksContent;
        this.commitList = commitList;
    }

    public List<RevCommit> getCommitList() {
        return this.commitList;
    }

    public String getCommitMessageIdAndContent() {
        StringBuilder builder = new StringBuilder();
        for (RevCommit commit : this.commitList) {
            String line = commit.getId() + " :: " + commit.getShortMessage();
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public boolean addToCommitMessageList(RevCommit commit) {
        return this.commitList.add(commit);
    }

    public void setCommitList(List<RevCommit> commitList) {
        this.commitList = commitList;
    }

    public Description getDescr() {
        return this.descr;
    }

    public String getHunksContent() {
        return this.hunksContent;
    }


}
