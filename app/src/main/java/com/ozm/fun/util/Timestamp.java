package com.ozm.fun.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Timestamp {
    public static final String UTC = "UTC";
    private static final long MILLIS = 1000;

    public static long getUTC() {
        return System.currentTimeMillis() / MILLIS;
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String getInterval(String dateFormat, long seconds) {
        TimeZone tz = TimeZone.getTimeZone(UTC);
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(tz);
        return df.format(new Date(seconds * MILLIS));
    }
}
