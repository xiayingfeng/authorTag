package descrscanner.format;

import static constant.RegexConstant.MONTH;

/**
 * @author Xia Yingfeng
 * @date 2022/4/11
 */
public enum MonthFormat {
    // There exists bug when first line is May/June/July, duplicated in CAP_FULL and CAP_SHORT

    MONTH_FORMAT(MONTH);
    public final String monthFormatValue;

    MonthFormat(String monthFormatValue) {
        this.monthFormatValue = monthFormatValue;
    }

}
