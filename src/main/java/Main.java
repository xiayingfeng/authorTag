import Entity.RepoTag;
import GitKit.CommitAnalyzer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Xia Yingfeng
 * @date 2021/12/10
 */
public class Main {
    private static final String ENV_PATH = "config/env";
    private static final CommitAnalyzer ANALYZER = new CommitAnalyzer();

    public static void main(String[] args) {
        batchAnalyze();
    }

    /** analyze all pairs logged in index.json*/
    private static void batchAnalyze() {
        ResourceBundle resource = ResourceBundle.getBundle(ENV_PATH);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        String indexPath = resource.getString("index_path");
        String reposDir = resource.getString("repos_dir");
        String originProportion = resource.getString("origin_proportion");
        String platform = resource.getString("platform");

        try {
            File indexFile = new File(indexPath);
            rootNode = mapper.readTree(indexFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<JsonNode> iterator =null;
        if (rootNode != null) {
            iterator = rootNode.iterator();
        }

        List<RepoTag> repoTagList = new ArrayList<>(212);
        if (iterator != null) {
            while (iterator.hasNext()) {
                JsonNode tmpNode = iterator.next();
                String childShortPath = tmpNode.get("child").asText();
                String parentShortPath = tmpNode.get("parent").asText();

                String childPath = getFullGitPath(reposDir, childShortPath, platform);
                String parentPath = getFullGitPath(reposDir, parentShortPath, platform);

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
        }
        writeRepoTags(originProportion, repoTagList, platform);
    }

    /** get full git  directory path in multiple platform*/
    private static String getFullGitPath(String reposDir, String shortPath, String platform) {
        String split = "";
        if ("windows".equals(platform)) {
            split = "//";
        } else if ("linux".equals(platform)) {
            split = "/";
        }

        String[] parts = shortPath.split("/");
        String specialDir = shortPath.replace("/", "__fdse__");
        String fullPath = reposDir + split + specialDir + split + parts[1] + split +".git" ;
        return fullPath;
    }

    /** write repo result tags to the destination json file */
    private static void writeRepoTags(String destJson, List<RepoTag> repoTagList, String platform) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode array = mapper.createArrayNode();
        for (RepoTag repoTag : repoTagList) {
            ObjectNode node = mapper.createObjectNode();

            node.put("self", getShortName(repoTag.getMyName(), platform));
            node.put("parent", getShortName(repoTag.getParentName(), platform));
            node.put("line_count", repoTag.getLineCount());
            node.put("parent_line_count", repoTag.getParentLineCount());
            node.put("origin_line_count", repoTag.getOriginLineCount());
            node.put("originProportion", repoTag.getOriginProportionInPercent());

            array.add(node);
        }

        try (OutputStream output = new FileOutputStream(destJson)){
            mapper.writeValue(output, array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** get short name of each repo in multiple platform*/
    private static String getShortName(String fullName, String platform) {
        String split = "";
        if ("windows".equals(platform)) {
            split = "//";
        } else if ("linux".equals(platform)) {
            split = "/";
        }
        String[] dirs = fullName.split(split);
        String shortName = dirs[dirs.length - 2].replace("__fdse__", "/");
        return shortName;
    }

}
