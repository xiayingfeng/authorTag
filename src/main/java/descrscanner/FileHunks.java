package descrscanner;

import org.eclipse.jgit.patch.HunkHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/4/13
 */
public class FileHunks {
    private final String absSrcFilePath;
    private final List<HunkHeader> hunksList;
    private int hunksCount;

    public FileHunks(String absSrcFilePath) {
        this.absSrcFilePath = absSrcFilePath;
        this.hunksList = new ArrayList<>();
        this.hunksCount = 0;
    }

    public FileHunks(String absSrcFilePath, List<HunkHeader> hunksList) {
        this.absSrcFilePath = absSrcFilePath;
        this.hunksList = hunksList;
        this.hunksCount = hunksList.size();
    }

    public String getAbsSrcFilePath() {
        return absSrcFilePath;
    }

    public List<HunkHeader> getHunksList() {
        return hunksList;
    }

    public int getHunksCount() {
        return hunksCount;
    }

    public boolean hasHunks() {
        return hunksCount > 0;
    }

    public void addHunk(HunkHeader hunk) {
        hunksList.add(hunk);
        hunksCount++;
    }


}
