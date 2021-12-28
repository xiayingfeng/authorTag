package Entity;

import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2021/12/27
 */
public class FileTag {
    private String fileFullName;
    private int size;
    private List<LineTag> lines;

    public FileTag(String fileFullName,  List<LineTag> lines) {
        this.fileFullName = fileFullName;
        this.size = lines.size();
        this.lines = lines;
    }

    public String getFileFullName() {
        return fileFullName;
    }

    public void setFileFullName(String fileFullName) {
        this.fileFullName = fileFullName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<LineTag> getLines() {
        return lines;
    }

    public void setLines(List<LineTag> lines) {
        this.lines = lines;
    }
}
