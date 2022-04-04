package constant;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Xia Yingfeng
 * @date 2022/3/9
 */
public class Constant {
    public static final String LIN = "Lin";
    public static final String WIN = "Win";
    private static final Logger logger = Logger.getLogger(Constant.class.getName());
    public static final String ENV_PATH = "config/env";

    public static final String INDEX_PATH, REPOS_DIR, ORIGIN_PROPORTION, PLATFORM;
    /**
     * before match, we should toUpperCase() the file name
     */
    public static final String[] KEY_WORDS = {"CHANGELOG", "CHANGE LOG", "README", "READ ME"};
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(ENV_PATH);

    static {
        PLATFORM = System.getProperty("os.name").substring(0, 3);
        INDEX_PATH = RESOURCE_BUNDLE.getString("index_path");
        REPOS_DIR = RESOURCE_BUNDLE.getString("repos_dir");
        ORIGIN_PROPORTION = RESOURCE_BUNDLE.getString("origin_proportion");

        logger.log(Level.CONFIG, "platform: " + PLATFORM);
        logger.log(Level.CONFIG, "repos_dir: " + REPOS_DIR);
        logger.log(Level.CONFIG, "index_path: " + INDEX_PATH);
    }

}
