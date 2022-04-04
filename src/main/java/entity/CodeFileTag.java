package entity;

import java.util.List;
import java.util.Set;

/**
 * @author Xia Yingfeng
 * @date 2022/3/9
 */
public class CodeFileTag extends FileTag {
    public CodeFileTag(String fileFullName, List<LineTag> lines, Set<String> shaSet) {
        super(fileFullName, lines, shaSet);
    }
}
