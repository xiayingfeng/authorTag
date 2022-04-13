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

    public final static HashMap<String, Integer> monthToIntMap = new HashMap<>();

    static {
        monthToIntMap.put("JANUARY", 1);
        monthToIntMap.put("FEBRUARY", 2);
        monthToIntMap.put("MARCH", 3);
        monthToIntMap.put("APRIL", 4);
        monthToIntMap.put("MAY", 5);
        monthToIntMap.put("JUNE", 6);
        monthToIntMap.put("JULY", 7);
        monthToIntMap.put("AUGUST", 8);
        monthToIntMap.put("SEPTEMBER", 9);
        monthToIntMap.put("OCTOBER", 10);
        monthToIntMap.put("NOVEMBER", 11);
        monthToIntMap.put("DECEMBER", 12);

        monthToIntMap.put("JAN.", 1);
        monthToIntMap.put("FEB.", 2);
        monthToIntMap.put("MAR.", 3);
        monthToIntMap.put("APR.", 4);
        monthToIntMap.put("MAY.", 5);
        monthToIntMap.put("JUN.", 6);
        monthToIntMap.put("JUL.", 7);
        monthToIntMap.put("AUG.", 8);
        monthToIntMap.put("SEPT.", 9);
        monthToIntMap.put("OCT.", 10);
        monthToIntMap.put("NOV.", 11);
        monthToIntMap.put("DEC.", 12);

        monthToIntMap.put("1", 1);
        monthToIntMap.put("2", 2);
        monthToIntMap.put("3", 3);
        monthToIntMap.put("4", 4);
        monthToIntMap.put("5", 5);
        monthToIntMap.put("6", 6);
        monthToIntMap.put("7", 7);
        monthToIntMap.put("8", 8);
        monthToIntMap.put("9", 9);
        monthToIntMap.put("10", 10);
        monthToIntMap.put("11", 11);
        monthToIntMap.put("12", 12);

        monthToIntMap.put("01", 1);
        monthToIntMap.put("02", 2);
        monthToIntMap.put("03", 3);
        monthToIntMap.put("04", 4);
        monthToIntMap.put("05", 5);
        monthToIntMap.put("06", 6);
        monthToIntMap.put("07", 7);
        monthToIntMap.put("08", 8);
        monthToIntMap.put("09", 9);

    }

    public final String monthFormatValue;

    MonthFormat(String monthFormatValue) {
        this.monthFormatValue = monthFormatValue;
    }

}
