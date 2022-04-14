package descrscanner.format;

/**
 * @author Xia Yingfeng
 * @date 2022/4/11
 */
public enum DayFormat {
    //
    NUM_DAY("(\\d{1,2})"),
    WORD_DAY("(\\d{1,2}(st|nd|rd|th))");


    public final String dayFormatValue;

    DayFormat(String dayFormatValue) {
        this.dayFormatValue = dayFormatValue;
    }

}
