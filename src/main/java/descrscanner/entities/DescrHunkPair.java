package descrscanner.entities;

/**
 * @author Xia Yingfeng
 * @date 2022/4/13
 */
public class DescrHunkPair {
    private final Description descr;
    private final String hunksContent;

    public DescrHunkPair(Description descr) {
        this.descr = descr;
        hunksContent = "";
    }

    public DescrHunkPair(Description descr, String hunksContent) {
        this.descr = descr;
        this.hunksContent = hunksContent;
    }

    public Description getDescr() {
        return descr;
    }

    public String getHunksContent() {
        return hunksContent;
    }
}
