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
    private static final CommitAnalyzer analyzer = new CommitAnalyzer();

    public static void main(String[] args) {
        ResourceBundle resource = ResourceBundle.getBundle(ENV_PATH);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        String indexPath = resource.getString("index_path");
        String reposDir = resource.getString("repos_dir");
        String originProportion = resource.getString("origin_proportion");

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

                String childPath = getFullGitPath(reposDir, childShortPath);
                String parentPath = getFullGitPath(reposDir, parentShortPath);

                Repository childRepo = null, parentRepo = null;
                try {
                    childRepo = new RepositoryBuilder().setGitDir(new File(childPath)).build();
                    parentRepo = new RepositoryBuilder().setGitDir(new File(parentPath)).build();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RepoTag repoTag = analyzer.getParentCodeSumByRepo(parentRepo, childRepo);
                repoTagList.add(repoTag);
            }
        }
        writeRepoTags(originProportion, repoTagList);
    }

    private static String getFullGitPath(String reposDir, String shortPath) {
        String[] parts = shortPath.split("/");
        String specialDir = shortPath.replace("/", "__fdse__");
        String fullPath = reposDir + "//" + specialDir + "//" + parts[1] + "//.git" ;
        return fullPath;
    }

    private static void writeRepoTags(String destJson, List<RepoTag> repoTagList) {
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

        try (OutputStream output = new FileOutputStream(destJson)){
            mapper.writeValue(output, array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getShortName(String fullName) {
        String[] dirs = fullName.split("//");
        String shortName = dirs[dirs.length - 2].replace("__fdse__", "/");
        return shortName;
    }


    private static void calcRepo(){

    }
}
