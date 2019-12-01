package com.nut.teamradar.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationRecordFileNameProcessor {
    private static final String TAG = "LocationRecordFileNameGenerator";
    private static final String FILE_NAME_PREFIX = "TeamRadar";
    private static final String DATE_FMT = "yyyyMMdd-";
    private static final String TIME_FMT = "HH:mm:ss";
    private static final String MS_FMT = ".SSS";
    private static final SimpleDateFormat mDateFmt =
            new SimpleDateFormat(DATE_FMT + TIME_FMT + MS_FMT);
    //private static final SimpleDateFormat mDateFmt = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
    public static String generateLocationRecordFileName() {
        return FILE_NAME_PREFIX + SeqNumGenerator.getNextZeroPadding();
    }
    public static String generateLocationRecordFileName(Date d) {
        return FILE_NAME_PREFIX + mDateFmt.format(d);
    }
    public static String generateLocationRecordFileName(Date d, String postfix) {
        return generateLocationRecordFileName(d) + postfix;
    }
    public static String getPrefix() {
        return FILE_NAME_PREFIX;
    }
    public static String getPostfix() {
        return MS_FMT;
    }
}
