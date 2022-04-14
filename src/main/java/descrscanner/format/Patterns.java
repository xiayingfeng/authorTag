package descrscanner.format;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static constant.RegexConstant.*;
import static descrscanner.format.DateFormat.*;


/**
 * @author Xia Yingfeng
 * @date 2022/4/8
 */
public class Patterns {
    private static final Logger logger = Logger.getLogger(Patterns.class.getName());

    private final String dateFormatRegex;
    boolean hasVerFormat;
    private String verFormatRegex;
    private VerFormat verFormat;
    private DateFormat dateFormat;

    public Patterns(String verFormatRegex, String dateFormatRegex) {
        this.verFormatRegex = verFormatRegex;
        this.dateFormatRegex = dateFormatRegex;
    }

    public Patterns(String str) {
        this.dateFormatRegex = extractDateFormat(str);
        if (containsRegex(VER, str)) {
            hasVerFormat = true;
            for (VerFormat verFormat : VerFormat.values()) {
                String tmpFormat = verFormat.verFormatValue;
                if (containsRegex(tmpFormat, str)) {
                    this.verFormat = verFormat;
                    this.verFormatRegex = tmpFormat;
                    break;
                }
            }
        } else {
            hasVerFormat = false;
        }
    }

    // determine whether this line of changelog contains a pattern
    public static boolean isFirstPatternStr(String string) {
        String str = string.toUpperCase();
        boolean ymd = containsRegex(YMD.dateFormatValue, str);
        boolean mdy = containsRegex(MDY.dateFormatValue, str);
        boolean dmy = containsRegex(DMY.dateFormatValue, str);
        boolean hasDate = ymd || mdy || dmy;
        return hasDate;
    }

    private static boolean containsRegex(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.find();
    }

    public String getDateFormatRegex() {
        return dateFormatRegex;
    }

    public String getVerFormatRegex() {
        return verFormatRegex;
    }

    public boolean isHasVerFormat() {
        return hasVerFormat;
    }

    public VerFormat getVerFormat() {
        return verFormat;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public String extractDate(String line) {
        Pattern p = Pattern.compile(dateFormatRegex);
        Matcher m = p.matcher(line);

        if (!m.find()) {
            logger.log(Level.SEVERE, "Date format not match in " + line);
        }

        String yearStr = m.group(dateFormat.yearIndex);
        String monthStr = m.group(dateFormat.monthIndex);
        String dayStr = m.group(dateFormat.dayIndex);

        monthStr = simplifyMonth(monthStr.toUpperCase());

        dayStr = dayStr.replaceAll(SUFFIX, "");

        String[] components = new String[6];
        components[2] = components[4] = "-";
        components[dateFormat.yearIndex] = yearStr;
        components[dateFormat.monthIndex] = monthStr;
        components[dateFormat.dayIndex] = dayStr;

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < components.length; i++) {
            builder.append(components[i]);
        }
        return builder.toString();
    }

    private String simplifyMonth(String monthStr) {
        if (!MONTH_TO_INT_MAP.containsKey(monthStr)) {
            logger.log(Level.SEVERE, "Illegal month :" + monthStr);
            // set an illegal month
            return "00";
        }
        return "" + MONTH_TO_INT_MAP.get(monthStr);
    }

    private String extractDateFormat(String str) {
        String dateFormat = "";
        if (containsRegex(YMD.dateFormatValue, str)) {
            this.dateFormat = YMD;
            dateFormat = getExactDateFormat(str, YMD);
        } else if (containsRegex(MDY.dateFormatValue, str)) {
            this.dateFormat = MDY;
            dateFormat = getExactDateFormat(str, MDY);
        } else if (containsRegex(DMY.dateFormatValue, str)) {
            this.dateFormat = DMY;
            dateFormat = getExactDateFormat(str, DMY);
        }
        return dateFormat;
    }

    private String getExactDateFormat(String str, DateFormat dateFormat) {
        StringBuilder stringBuffer = new StringBuilder();
        Pattern pattern = Pattern.compile(dateFormat.dateFormatValue);
        Matcher matcher = pattern.matcher(str);

        if (!matcher.find()) {
            logger.log(Level.SEVERE, "Date format not match in " + str);
        }

        String dateStr = matcher.group(0);
        String split = "(" + extractSplit(dateStr) + ")";

        String monthStr = matcher.group(dateFormat.monthIndex);
        String dayStr = matcher.group(dateFormat.dayIndex);
        /*
        split dateStr into array, we already have the sequence of year, month and day
        YEAR + SPLIT + MONTH + SPLIT +  DAY
        */
        String monthFormat = extractMonthFormat(monthStr);
        String dayFormat = extractDayFormat(dayStr);
        String[] components = new String[6];
        components[2] = split;
        components[4] = split;
        components[dateFormat.dayIndex] = dayFormat;
        components[dateFormat.monthIndex] = monthFormat;
        components[dateFormat.yearIndex] = YEAR;
        for (int i = 1; i < components.length; i++) {
            stringBuffer.append(components[i]);
        }

        return stringBuffer.toString();
    }

    private String extractSplit(String dateStr) {
        String split = "";
        Pattern pattern = Pattern.compile(SPLIT);
        Matcher matcher = pattern.matcher(dateStr);
        if (matcher.find()) {
            split = matcher.group();
        }
        return split;
    }

    private String extractDayFormat(String dateStr) {
        String dayFormat = "";
        for (DayFormat format : DayFormat.values()) {
            String formatValue = format.dayFormatValue;
            if (containsRegex(formatValue, dateStr)) {
                dayFormat = formatValue;
                break;
            }
        }
        return dayFormat;
    }

    private String extractMonthFormat(String dateStr) {
        String monthFormat = "";
        for (MonthFormat format : MonthFormat.values()) {
            String formatValue = format.monthFormatValue;
            if (containsRegex(formatValue, dateStr)) {
                monthFormat = formatValue;
                break;
            }
        }
        return monthFormat;
    }

    public boolean isComplyWithPatterns(String str) {
        boolean fitDatePat = containsRegex(dateFormatRegex, str);
        if (hasVerFormat) {
            boolean fitVerPat = containsRegex(verFormatRegex, str);
            return fitDatePat && fitVerPat;
        }
        return fitDatePat;
    }
}
