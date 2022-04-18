package descrscanner.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Xia Yingfeng
 * @date 2022/3/28
 */

public class Description {
    private final Date leftEnd;
    private final Date rightEnd;
    private final List<String> descrLines;

    public Description(Date leftEnd, Date rightEnd, List<String> descrLines) {
        this.leftEnd = leftEnd;
        this.rightEnd = rightEnd;
        this.descrLines = descrLines;
    }

    public Description(Date rightEnd, ArrayList<String> descrLines) {
        // set leftEnd as January 1, 1970, 00:00:00 GMT
        this.leftEnd = new Date(0);
        this.rightEnd = rightEnd;
        this.descrLines = descrLines;
    }

    public Date getLeftEnd() {
        return leftEnd;
    }

    public Date getRightEnd() {
        return rightEnd;
    }

    public String getDescrContent() {
        StringBuilder builder = new StringBuilder();
        for (String line : descrLines) {
            builder.append(line);
        }
        return builder.toString();
    }
}
