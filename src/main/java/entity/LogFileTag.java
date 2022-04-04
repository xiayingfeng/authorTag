package entity;

import java.util.List;
import java.util.Set;

/**
 * @author Xia Yingfeng
 * @date 2022/3/9
 */
public class LogFileTag extends TextFileTag {
    private String keyWord;
    private String[] logContent;
    /**
     * from forking as true, and forked false.
     */
    private boolean[] logOwnership;

    public LogFileTag(String fileFullName, List<LineTag> lines, Set<String> shaSet) {
        super(fileFullName, lines, shaSet);
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String[] getLogContent() {
        return logContent;
    }

    public void setLogContent(String[] logContent) {
        this.logContent = logContent;
    }

    public boolean[] getLogOwnership() {
        return logOwnership;
    }

    public void setLogOwnership(boolean[] logOwnership) {
        this.logOwnership = logOwnership;
    }
}
