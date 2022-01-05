package entity;

import java.util.List;
import java.util.Set;

/**
 * @author Xia Yingfeng
 * @date 2021/12/27
 */
public class FileTag {
    private final String fileFullName;
    private final int size;
    private final List<LineTag> lines;
    private final Set<String> shaSet;
    private boolean[] bitMap;


    public FileTag(String fileFullName,  List<LineTag> lines, Set<String> shaSet) {
        this.fileFullName = fileFullName;
        this.size = lines.size();
        this.lines = lines;
        this.shaSet = shaSet;
        this.bitMap = new boolean[this.size];
    }

    public String getFileFullName() {
        return fileFullName;
    }

    public int getSize() {
        return size;
    }

    // better to return a copy instead of original object
    public List<LineTag> getLines() {
        return lines;
    }

    public boolean containsCommit(String sha) {
        return shaSet.contains(sha);
    }

    public boolean[] getBitMap() {
        return bitMap;
    }

    public void setBitMap(boolean[] bitMap) {
        this.bitMap = bitMap;
    }
}
