package Entity;

/**
 * @author Xia Yingfeng
 * @date 2021/12/27
 */
public class LineTag {
    private int line;
    private String content;
    //short SHA
    private String sha;
    private String author;

    public LineTag(int line, String content, String sha, String author) {
        this.line = line;
        this.content = content;
        this.sha = sha;
        this.author = author;
    }

    public int getLine() {
        return line;
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
}
