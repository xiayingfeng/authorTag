package descrscanner.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/4/13
 */
public class DescrHunkPair {
    private final Description descr;
    private final String hunksContent;
    private List<String> commitMessageList;

    public DescrHunkPair(Description descr) {
        this.descr = descr;
        this.hunksContent = "";
        this.commitMessageList = new ArrayList<>();
    }

    public DescrHunkPair(Description descr, String hunksContent) {
        this.descr = descr;
        this.hunksContent = hunksContent;
        this.commitMessageList = new ArrayList<>();
    }

    public DescrHunkPair(Description descr, String hunksContent, List<String> commitMessageList) {
        this.descr = descr;
        this.hunksContent = hunksContent;
        this.commitMessageList = commitMessageList;
    }

    public List<String> getCommitMessageList() {
        return commitMessageList;
    }

    public boolean addToCommitMessageList(String commitMessage) {
        return this.commitMessageList.add(commitMessage);
    }

    public void setCommitMessageList(List<String> commitMessageList) {
        this.commitMessageList = commitMessageList;
    }

    public Description getDescr() {
        return descr;
    }

    public String getHunksContent() {
        return hunksContent;
    }


}
