package descrscanner;

import descrscanner.entities.DescriptionFile;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/3/28
 */
public interface IDescrScanner {
    /**
     * get list of file containing modification description of specified repository
     *
     * @param repoName repo name with owner eg: google/dagger
     * @return file list
     */
    List<File> getDescrFileList(String repoName);

    /**
     * get list of Description of specified repository
     *
     * @param repoName repo name with owner eg: google/dagger
     * @return description blocks list
     */
    List<DescriptionFile> getDescrList(String repoName) throws GitAPIException, IOException;

}
