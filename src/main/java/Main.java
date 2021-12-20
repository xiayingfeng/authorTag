import GitKit.CommitAnalyzer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * @author Xia Yingfeng
 * @date 2021/12/10
 */
public class Main {
    public static void main(String[] args) {
        CommitAnalyzer analyzer = new CommitAnalyzer();
        Repository left = null, right = null;
        try {
            left = new RepositoryBuilder()
                    .setGitDir(new File("src/main/resources/repos/google__fdse__dagger/dagger/.git"))
                    .build();
            right = new RepositoryBuilder()
                    .setGitDir(new File("src/main/resources/repos/square__fdse__dagger/dagger/.git"))
                    .build();
            boolean test = analyzer.isParentRepo(left, right);
            System.out.println(test);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (left != null){
                left.close();
            }
            if (right != null) {
                right.close();
            }
        }
    }

}
