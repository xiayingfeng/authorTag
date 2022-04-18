package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Xia Yingfeng
 * @date 2022/4/19
 */
public class MyFileUtil {
    public static Iterator<JsonNode> getJsonItrFrom(String jsonFileName) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;

        try {
            File indexFile = new File(jsonFileName);
            rootNode = mapper.readTree(indexFile);
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
