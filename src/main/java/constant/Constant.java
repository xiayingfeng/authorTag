package constant;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Xia Yingfeng
 * @date 2022/3/9
 */
public final class Constant {
    public static final String REPOS_DIR, PLATFORM;
    //    public static final String LIN = "Lin";
    public static final String WIN_PRE = "win_", LIN_PRE = "lin_", ACT_PRE;
    public static final String WIN = "Win";
    public static final String FDSE = "__fdse__";
    public static final String ENV_PATH = "config/env";
    public static final String DIFF_INDEX_PATH, DIFF_OUTPUT_PATH;
    public static final String DESCR_INDEX_PATH, DESCR_OUTPUT_PATH, DESCR_OUT_DETAILS;
    private static final Logger logger = Logger.getLogger(Constant.class.getName());
    /**
     * before match, we should toUpperCase() the file name
     */
    public static final String[] KEY_WORDS = {"CHANGELOG", "CHANGE LOG", "README", "READ ME"};
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(ENV_PATH);

    static {
        PLATFORM = System.getProperty("os.name").substring(0, 3);
        ACT_PRE = PLATFORM.equals(WIN) ? WIN_PRE : LIN_PRE;
        REPOS_DIR = RESOURCE_BUNDLE.getString(ACT_PRE + "repos_dir");
        logger.log(Level.CONFIG, "platform: " + PLATFORM);
        logger.log(Level.CONFIG, "repos_dir: " + REPOS_DIR);

        DIFF_INDEX_PATH = RESOURCE_BUNDLE.getString(ACT_PRE + "diff_index_path");
        DIFF_OUTPUT_PATH = RESOURCE_BUNDLE.getString(ACT_PRE + "diff_output_path");
        logger.log(Level.CONFIG, "diff_index_path: " + DIFF_INDEX_PATH);
        logger.log(Level.CONFIG, "diff_output_path: " + DIFF_OUTPUT_PATH);

        DESCR_INDEX_PATH = RESOURCE_BUNDLE.getString(ACT_PRE + "descr_index_path");
        DESCR_OUTPUT_PATH = RESOURCE_BUNDLE.getString(ACT_PRE + "descr_output_path");
        DESCR_OUT_DETAILS = RESOURCE_BUNDLE.getString(ACT_PRE + "descr_out_details");
        logger.log(Level.CONFIG, "descr_index_path: " + DESCR_INDEX_PATH);
        logger.log(Level.CONFIG, "descr_output_path: " + DESCR_OUTPUT_PATH);
        logger.log(Level.CONFIG, "descr_out_details: " + DESCR_OUT_DETAILS);
    }

}
