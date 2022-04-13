package descrscanner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/4/13
 */
public class DescrHunkPair {
    private final Description descr;
    private final List<FileHunks> fileHunksList;

    public DescrHunkPair(Description descr) {
        this.descr = descr;
        fileHunksList = new ArrayList<>();
    }

    public DescrHunkPair(Description descr, List<FileHunks> fileHunksList) {
        this.descr = descr;
        this.fileHunksList = fileHunksList;
    }

    public Description getDescr() {
        return descr;
    }

    public List<FileHunks> getFileHunksList() {
        return fileHunksList;
    }

    public void addFileHunks(FileHunks fileHunks) {
        fileHunksList.add(fileHunks);
    }
}
