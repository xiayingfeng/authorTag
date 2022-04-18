package descrscanner.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/4/13
 */
public class FileHunks {
    private final String absSrcFilePath;
    private final List<String> hunksList;
    private int hunksCount;

    public FileHunks(String absSrcFilePath) {
        this.absSrcFilePath = absSrcFilePath;
        this.hunksList = new ArrayList<>();
        this.hunksCount = 0;
    }

    public FileHunks(String absSrcFilePath, List<String> hunksList) {
        this.absSrcFilePath = absSrcFilePath;
        this.hunksList = hunksList;
        this.hunksCount = hunksList.size();
    }

    public String getAbsSrcFilePath() {
        return absSrcFilePath;
    }

    public List<String> getHunksList() {
        return hunksList;
    }

    public int getHunksCount() {
        return hunksCount;
    }

    public boolean hasHunks() {
        return hunksCount > 0;
    }

    public void addHunk(String hunk) {
        hunksList.add(hunk);
        hunksCount++;
    }


}
