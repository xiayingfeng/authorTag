package descrscanner.entities;

import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/4/13
 */
public class DescrHunkPair {
    private final Description descr;
    private final String hunksContent;
    private List<String> commitMessageList;

    public DescrHunkPair(Description descr, String hunksContent, List<String> commitMessageList) {
        this.descr = descr;
        this.hunksContent = hunksContent;
        this.commitMessageList = commitMessageList;
    }

    public List<String> getCommitMessageList() {
        return this.commitMessageList;
    }

    public String getCommitMessageContent() {
        StringBuilder builder = new StringBuilder();
        for (String line : this.commitMessageList) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public boolean addToCommitMessageList(String commitMessage) {
        return this.commitMessageList.add(commitMessage);
    }

    public void setCommitMessageList(List<String> commitMessageList) {
        this.commitMessageList = commitMessageList;
    }

    public Description getDescr() {
        return this.descr;
    }

    public String getHunksContent() {
        return this.hunksContent;
    }


}
