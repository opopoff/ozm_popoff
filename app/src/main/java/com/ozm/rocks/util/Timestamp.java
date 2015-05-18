package com.ozm.rocks.util;

public class Timestamp {
    private static final long MILLIS = 1000;

    public static long getUTC() {
        return System.currentTimeMillis() / MILLIS;
    }

}
