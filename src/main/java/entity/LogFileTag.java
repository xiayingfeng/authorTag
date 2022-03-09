package entity;

import java.util.List;
import java.util.Set;

/**
 * @author Xia Yingfeng
 * @date 2022/3/9
 */
public class LogFileTag extends TextFileTag {
    public LogFileTag(String fileFullName, List<LineTag> lines, Set<String> shaSet) {
        super(fileFullName, lines, shaSet);
    }
}
