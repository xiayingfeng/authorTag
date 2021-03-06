package diffscanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entity.RepoTag;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import utils.MyFileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static constant.Constant.DIFF_INDEX_PATH;
import static constant.Constant.FDSE;

/**
 * @author Xia Yingfeng
 * @date 2021/12/10
 */
public class DiffPortal {

    private static final CommitAnalyzer ANALYZER = new CommitAnalyzer();

    public static void main(String[] args) {
        batchAnalyze();
    }

    /**
     * analyze all pairs logged in index.json
     */
    private static void batchAnalyze() {
        Iterator<JsonNode> iterator = MyFileUtil.getJsonItrFrom(DIFF_INDEX_PATH);

        List<RepoTag> repoTagList = new ArrayList<>();
        if (iterator == null) {
            return;
        }
        while (iterator.hasNext()) {
            JsonNode tmpNode = iterator.next();
            String childShortPath = tmpNode.get("child").asText();
            String parentShortPath = tmpNode.get("parent").asText();

            String childPath = getFullGitPath(childShortPath);
            String parentPath = getFullGitPath(parentShortPath);

            Repository childRepo = null, parentRepo = null;
            try {
                childRepo = new RepositoryBuilder().setGitDir(new File(childPath)).build();
                parentRepo = new RepositoryBuilder().setGitDir(new File(parentPath)).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RepoTag repoTag = ANALYZER.getParentCodeSumByRepo(parentRepo, childRepo);
            repoTagList.add(repoTag);
        }

        writeRepoTags(repoTagList);
    }

    /**
     * get full git  directory path in multiple platform
     */
    private static String getFullGitPath(String shortPath) {
        String split = File.separator;

        String[] parts = shortPath.split("/");
        String specialDir = shortPath.replace("/", FDSE);
        return constant.Constant.REPOS_DIR + split + specialDir + split + parts[1] + split + ".git";
    }

    /**
     * write repo result tags to the destination json file
     */
    private static void writeRepoTags(List<RepoTag> repoTagList) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode array = mapper.createArrayNode();
        for (RepoTag repoTag : repoTagList) {
            ObjectNode node = mapper.createObjectNode();

            node.put("self", getShortName(repoTag.getMyName()));
            node.put("parent", getShortName(repoTag.getParentName()));
            node.put("line_count", repoTag.getLineCount());
            node.put("parent_line_count", repoTag.getParentLineCount());
            node.put("origin_line_count", repoTag.getOriginLineCount());
            node.put("originProportion", repoTag.getOriginProportionInPercent());

            array.add(node);
        }

        try (OutputStream output = new FileOutputStream(constant.Constant.DIFF_OUTPUT_PATH)) {
            mapper.writeValue(output, array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** get short name of each repo in multiple platform*/
    private static String getShortName(String fullName) {
        String split = File.separator;

        String[] dirs = fullName.split(split);
        return dirs[dirs.length - 2].replace(FDSE, "/");
    }

}
