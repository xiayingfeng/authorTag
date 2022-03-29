package descrscanner;

import java.io.File;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/3/28
 */
public interface IDescrScanner {
    /**
     * get list of file containing modification description of specified repository
     *
     * @return file list
     */
    List<File> getDescrFileList();

    /**
     * get list of Description of specified repository
     *
     * @return description blocks list
     */
    List<Description> getDescrList();

}
