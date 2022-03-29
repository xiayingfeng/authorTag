package entity;

/**
 * @author Xia Yingfeng
 * @date 2021/12/27
 */
public class LineTag {
    private final int lineNo;
    private final String content;
    /**
     * short SHA
     */
    private final String sha;
    private final String author;
    private final String timeStamp;

    public LineTag(int lineNo, String content, String sha, String author, String timeStamp) {
        this.lineNo = lineNo;
        this.content = content;
        this.sha = sha;
        this.author = author;
        this.timeStamp = timeStamp;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getContent() {
        return content;
    }

    public String getSha() {
        return sha;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
