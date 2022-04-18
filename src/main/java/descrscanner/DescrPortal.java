package descrscanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import descrscanner.entities.DescrHunkPair;
import descrscanner.entities.Description;
import descrscanner.entities.DescriptionFile;
import org.eclipse.jgit.api.errors.GitAPIException;
import utils.MyFileUtil;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static constant.Constant.*;

/**
 * @author Xia Yingfeng
 * @date 2022/3/30
 */
public class DescrPortal {
    private static final Logger logger = Logger.getLogger(DescrScanner.class.getName());

    public static void main(String[] args) {
        Iterator<JsonNode> repoItr = MyFileUtil.getJsonItrFrom(DESCR_INDEX_PATH);
        if (repoItr == null) {
            logger.log(Level.SEVERE, "Iterator is null");
            return;
        }

        HashMap<String, List<DescrHunkPair>> repoDescrMap = new HashMap<>(50);
        while (repoItr.hasNext()) {
            DescrScanner descrScanner = new DescrScanner();
            JsonNode node = repoItr.next();
            String repoName = node.get("name").asText();
            List<DescrHunkPair> descrHunkPairs = null;
            try {
                List<DescriptionFile> descrFiles = descrScanner.getDescrList(repoName);
                logger.log(Level.INFO, "Description extracting succeed: " + repoName);
                descrHunkPairs = descrScanner.getDescrHunkPairs(descrFiles);
            } catch (GitAPIException | IOException e) {
                logger.log(Level.SEVERE, "Description extracting failed: " + repoName);
                e.printStackTrace();
            }
            if (descrHunkPairs == null) {
                logger.log(Level.SEVERE, "No <description, hunks> pairs found: " + repoName);
                descrHunkPairs = new ArrayList<>();
            }
            repoDescrMap.put(repoName, descrHunkPairs);
        }
        try {
            writeAsJson(repoDescrMap);
            writeAsTxt(repoDescrMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeAsJson(HashMap<String, List<DescrHunkPair>> repoDescrMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode array = mapper.createArrayNode();

        for (Map.Entry<String, List<DescrHunkPair>> entry : repoDescrMap.entrySet()) {
            String repoName = entry.getKey();
            List<DescrHunkPair> pairs = entry.getValue();

            ObjectNode node = mapper.createObjectNode();

            node.put("repo name", repoName);

            ArrayNode pairsArray = mapper.createArrayNode();
            for (DescrHunkPair pair : pairs) {
                ObjectNode pairNode = mapper.createObjectNode();
                Description description = pair.getDescr();
                pairNode.put("begin time", description.getLeftEnd().toString());
                pairNode.put("end time", description.getRightEnd().toString());
                pairNode.put("description", description.getDescrContent());
                pairNode.put("hunks", pair.getHunksContent());
                pairsArray.add(pairNode);
            }
            node.set("pairs", pairsArray);
            array.add(node);
        }

        File outFile = new File(constant.Constant.DESCR_OUTPUT_PATH);
        if (!outFile.exists()) {
            boolean isOutFileCreated = outFile.createNewFile();
            if (!isOutFileCreated) {
                logger.log(Level.SEVERE, "decr json file create failed: " + outFile.getAbsolutePath());
            }
        }
        try (OutputStream output = new FileOutputStream(outFile)) {
            mapper.writeValue(output, array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeAsTxt(HashMap<String, List<DescrHunkPair>> repoDescrMap) throws IOException {
        String paraSeparator = "=====================================================================";
        for (Map.Entry<String, List<DescrHunkPair>> entry : repoDescrMap.entrySet()) {
            String repoName = entry.getKey();
            List<DescrHunkPair> pairs = entry.getValue();

            String fdFileName = repoName.replace("/", FDSE);
            File detailFile = new File(DESCR_OUT_DETAILS + File.separator + fdFileName + ".txt");
            if (!detailFile.exists()) {
                boolean isDetailFileCreated = detailFile.createNewFile();
                if (!isDetailFileCreated) {
                    logger.log(Level.SEVERE, "Detail file created failed: " + detailFile.getAbsolutePath());
                }
            }
            FileWriter writer = new FileWriter(detailFile);

            writer.append("repo name: ").append(repoName).append("\n");
            for (DescrHunkPair pair : pairs) {
                Description description = pair.getDescr();

                writer.append(paraSeparator).append("\n")
                        .append("start time: ").append(description.getLeftEnd().toString()).append("\n")
                        .append("end time: ").append(description.getRightEnd().toString()).append("\n")
                        .append(description.getDescrContent()).append("\n")
                        .append(pair.getHunksContent()).append("\n");
            }
        }
    }
}
