package com.nut.teamradar.util;

import java.text.DecimalFormat;

public class SeqNumGenerator {
    private static short mStart = -1;
    private static final DecimalFormat fmt = new DecimalFormat("00000");
    public static short getNext() {
        mStart = mStart < Short.MAX_VALUE ? mStart : -1;
        return ++mStart;
    }
    public static String getNextZeroPadding() {
        return fmt.format(getNext());
    }
}
