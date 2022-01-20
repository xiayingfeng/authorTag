package constant;

import java.util.ResourceBundle;

public class Constant {
    public static final String LIN = "linux";
    public static final String WIN = "windows";
    public static final String ENV_PATH = "config/env";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(ENV_PATH);
    public static final String INDEX_PATH, REPOS_DIR, ORIGIN_PROPORTION, PLATFORM;
    static {
        PLATFORM = RESOURCE_BUNDLE.getString("platform");
        INDEX_PATH = RESOURCE_BUNDLE.getString("index_path");
        REPOS_DIR = RESOURCE_BUNDLE.getString("repos_dir");
        ORIGIN_PROPORTION = RESOURCE_BUNDLE.getString("origin_proportion");
    }

}
