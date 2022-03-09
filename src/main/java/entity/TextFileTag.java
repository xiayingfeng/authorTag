package entity;

import java.util.List;
import java.util.Set;

/**
 * @author Xia Yingfeng
 * @date 2022/3/9
 */
public class TextFileTag extends FileTag {
    public TextFileTag(String fileFullName, List<LineTag> lines, Set<String> shaSet) {
        super(fileFullName, lines, shaSet);
    }
}
