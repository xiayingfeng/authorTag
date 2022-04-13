package descrscanner.format;

import java.util.HashMap;

/**
 * @author Xia Yingfeng
 * @date 2022/4/11
 */
public enum MonthFormat {
    // There exists bug when first line is May/June/July, duplicated in CAP_FULL and CAP_SHORT
    NUM_MONTH("(\\d{1,2})"),
    UPPER_FULL_MONTH("(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER)"),
    LOWER_FULL_MONTH("(january|february|march|april|may|june|july|august|september|october|november|december)"),
    CAP_FULL_MONTH("(January|February|March|April|May|June|July|August|September|October|November|December)"),
    CAP_SHORT_MONTH("(Jan.|Feb.|Mar.|Apr.|May.|Jun.|Jul.|Aug.|Sept.|Oct.|Nov.|Dec.)");

    public final static HashMap<String, Integer> MONTH_TO_INT_MAP = new HashMap<>();

    static {
        MONTH_TO_INT_MAP.put("JANUARY", 1);
        MONTH_TO_INT_MAP.put("FEBRUARY", 2);
        MONTH_TO_INT_MAP.put("MARCH", 3);
        MONTH_TO_INT_MAP.put("APRIL", 4);
        MONTH_TO_INT_MAP.put("MAY", 5);
        MONTH_TO_INT_MAP.put("JUNE", 6);
        MONTH_TO_INT_MAP.put("JULY", 7);
        MONTH_TO_INT_MAP.put("AUGUST", 8);
        MONTH_TO_INT_MAP.put("SEPTEMBER", 9);
        MONTH_TO_INT_MAP.put("OCTOBER", 10);
        MONTH_TO_INT_MAP.put("NOVEMBER", 11);
        MONTH_TO_INT_MAP.put("DECEMBER", 12);

        MONTH_TO_INT_MAP.put("JAN.", 1);
        MONTH_TO_INT_MAP.put("FEB.", 2);
        MONTH_TO_INT_MAP.put("MAR.", 3);
        MONTH_TO_INT_MAP.put("APR.", 4);
        MONTH_TO_INT_MAP.put("MAY.", 5);
        MONTH_TO_INT_MAP.put("JUN.", 6);
        MONTH_TO_INT_MAP.put("JUL.", 7);
        MONTH_TO_INT_MAP.put("AUG.", 8);
        MONTH_TO_INT_MAP.put("SEPT.", 9);
        MONTH_TO_INT_MAP.put("OCT.", 10);
        MONTH_TO_INT_MAP.put("NOV.", 11);
        MONTH_TO_INT_MAP.put("DEC.", 12);

        MONTH_TO_INT_MAP.put("1", 1);
        MONTH_TO_INT_MAP.put("2", 2);
        MONTH_TO_INT_MAP.put("3", 3);
        MONTH_TO_INT_MAP.put("4", 4);
        MONTH_TO_INT_MAP.put("5", 5);
        MONTH_TO_INT_MAP.put("6", 6);
        MONTH_TO_INT_MAP.put("7", 7);
        MONTH_TO_INT_MAP.put("8", 8);
        MONTH_TO_INT_MAP.put("9", 9);
        MONTH_TO_INT_MAP.put("10", 10);
        MONTH_TO_INT_MAP.put("11", 11);
        MONTH_TO_INT_MAP.put("12", 12);

        MONTH_TO_INT_MAP.put("01", 1);
        MONTH_TO_INT_MAP.put("02", 2);
        MONTH_TO_INT_MAP.put("03", 3);
        MONTH_TO_INT_MAP.put("04", 4);
        MONTH_TO_INT_MAP.put("05", 5);
        MONTH_TO_INT_MAP.put("06", 6);
        MONTH_TO_INT_MAP.put("07", 7);
        MONTH_TO_INT_MAP.put("08", 8);
        MONTH_TO_INT_MAP.put("09", 9);

    }

    public final String monthFormatValue;

    MonthFormat(String monthFormatValue) {
        this.monthFormatValue = monthFormatValue;
    }

}
