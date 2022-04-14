package descrscanner.format;

import static constant.RegexConstant.*;


/**
 * @author Xia Yingfeng
 * @date 2022/4/11
 */
public enum DateFormat {
    //
    YMD(YEAR + SPLIT + MONTH + SPLIT + DAY, 1, 3, 5),
    MDY(MONTH + SPLIT + DAY + SPLIT + YEAR, 5, 1, 3),
    DMY(DAY + SPLIT + MONTH + SPLIT + YEAR, 5, 3, 1);

    public final String dateFormatValue;
    public final int yearIndex;
    public final int monthIndex;
    public final int dayIndex;

    DateFormat(String dateFormatValue, int yearIndex, int monthIndex, int dayIndex) {
        this.dateFormatValue = dateFormatValue;
        this.yearIndex = yearIndex;
        this.monthIndex = monthIndex;
        this.dayIndex = dayIndex;
    }
}
