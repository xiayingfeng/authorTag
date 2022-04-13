package descrscanner.format;

/**
 * @author Xia Yingfeng
 * @date 2022/4/11
 */

public enum VerFormat {

    /**
     * possible version:
     * d.d.d      \d+[.]\d+[.]\d+
     * v d.d.d    [v][ ]\d+[.]\d+[.]\d+
     * vd.d.d     [v]\d+[.]\d+[.]\d+
     * version d.d.d
     * Version d.d
     * Version d.d.d
     * vd
     * <p>
     * (VERSION|V)?\s?\d+(\.\d+)*
     */
    VER_0("\\d+(\\.\\d+)*"),
    VER_10("v\\d+(\\.\\d+)*"),
    VER_11("v \\d+(\\.\\d+)*"),
    VER_20("V\\d+(\\.\\d+)*"),
    VER_21("V \\d+(\\.\\d+)*"),
    VER_30("Version\\d+(\\.\\d+)*"),
    VER_31("Version \\d+(\\.\\d+)*"),
    VER_40("version\\d+(\\.\\d+)*"),
    VER_41("version \\d+(\\.\\d+)*"),
    VER_50("VERSION\\d+(\\.\\d+)*"),
    VER_51("VERSION \\d+(\\.\\d+)*");

    public final String verFormatValue;


    VerFormat(String verFormatValue) {
        this.verFormatValue = verFormatValue;

    }
}

