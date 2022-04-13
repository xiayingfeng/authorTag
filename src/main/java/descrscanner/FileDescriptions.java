package descrscanner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/4/12
 */
public class FileDescriptions {
    private final String absDescrFilePath;
    private final List<Description> descrList;
    private int descrCount;

    public FileDescriptions(String absDescrFilePath, List<Description> descrList) {
        this.absDescrFilePath = absDescrFilePath;
        this.descrList = descrList;
        descrCount = descrList.size();
    }

    public FileDescriptions(String absDescrFilePath) {
        this.absDescrFilePath = absDescrFilePath;
        descrList = new ArrayList<>();
        descrCount = 0;
    }

    public void addDescription(Description descr) {
        descrList.add(descr);
        descrCount++;
    }

    public String getAbsDescrFilePath() {
        return absDescrFilePath;
    }

    public List<Description> getDescrList() {
        return descrList;
    }

    public int getDescrCount() {
        return descrCount;
    }

    public boolean hasDescr() {
        return descrCount > 0;
    }
}
