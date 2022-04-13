package descrscanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static constant.Constant.DESCR_INDEX_PATH;

/**
 * @author Xia Yingfeng
 * @date 2022/3/30
 */
public class DescrPortal {
    private static final Logger logger = Logger.getLogger(DescrScanner.class.getName());

    public static void main(String[] args) {
        Iterator<JsonNode> repoItr = getRepoItr();
        if (repoItr == null) {
            logger.log(Level.SEVERE, "Iterator is null");
            return;
        }
        while (repoItr.hasNext()) {
            DescrScanner descrScanner = new DescrScanner();
            JsonNode node = repoItr.next();
            String repoName = node.get("name").asText();
            try {
                List<DescriptionFile> descrFiles = descrScanner.getDescrList(repoName);
                logger.log(Level.INFO, "Description extracting succeed: " + repoName);
                List<DescrHunkPair> descrHunkPairs = descrScanner.getDescrHunkPairs(descrFiles);


            } catch (GitAPIException | IOException e) {
                logger.log(Level.SEVERE, "Description extracting failed: " + repoName);
                e.printStackTrace();
            }
        }

    }


    private static Iterator<JsonNode> getRepoItr() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;

        try {
            File descrIndexFile = new File(DESCR_INDEX_PATH);
            rootNode = mapper.readTree(descrIndexFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<JsonNode> iterator = null;
        if (rootNode != null) {
            iterator = rootNode.iterator();
        }

        return iterator;
    }
}
