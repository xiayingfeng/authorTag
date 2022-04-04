package utils;

/**
 * @author xiayingfeng
 */
public class Runtimer {
    private static Runtime runtime;

    private Runtimer(){
//        runtime = Runtime.getRuntime();
    }

    public static Runtime getRuntime() {
        if (runtime == null) {
            runtime = Runtime.getRuntime();
        }
        return runtime;
    }
}
