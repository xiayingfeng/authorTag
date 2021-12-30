package Entity;

import java.text.NumberFormat;

/**
 * @author Xia Yingfeng
 * @date 2021/12/30
 */
public class RepoTag {
    private String parentName;
    private String myName;
    private int lineCount;
    private int parentLineCount;
    private int originLineCount;
    private float originProportion;

    public RepoTag(){}

    public RepoTag(String parentName, String myName, int lineCount, int parentLineCount) {
        this.parentName = parentName;
        this.myName = myName;
        this.lineCount = lineCount;
        this.parentLineCount = parentLineCount;
        this.originLineCount = lineCount - parentLineCount;
        this.originProportion = (float) originLineCount / lineCount;
    }

    public String getParentName() {
        return parentName;
    }

    public String getMyName() {
        return myName;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getParentLineCount() {
        return parentLineCount;
    }

    public int getOriginLineCount() {
        return originLineCount;
    }

    public float getOriginProportionInFloat() {
        return originProportion;
    }

    public String getOriginProportionInPercent() {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(2);
        return format.format(originProportion);
    }

    public RepoTag setParentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    public RepoTag setMyName(String myName) {
        this.myName = myName;
        return this;
    }

    public RepoTag setLineCount(int lineCount) {
        this.lineCount = lineCount;
        return this;
    }

    public RepoTag setParentLineCount(int parentLineCount) {
        this.parentLineCount = parentLineCount;
        return this;
    }

    public RepoTag setOriginLineCount(int originLineCount) {
        this.originLineCount = originLineCount;
        return this;
    }
}
