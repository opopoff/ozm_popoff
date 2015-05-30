package com.ozm.rocks.util;

import android.support.annotation.Nullable;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

public final class Strings {
    public static final String DOT = ".";
    public static final String COLON = ":";
    public static final String SLASH = "/";
    public static final String ENTER = "\n";
    public static final String EMPTY = "";

    private static final String ALGORITHM_MD5 = "MD5";

    private Strings() {
        // No instances.
    }

    public static boolean isBlank(CharSequence string) {
        return string == null || string.toString().trim().length() == 0;
    }

    public static String valueOrDefault(String string, String defaultString) {
        return isBlank(string) ? defaultString : string;
    }

    public static String truncateAt(String string, int length) {
        return string.length() > length ? string.substring(0, length) : string;
    }

    @Nullable
    public static String md5(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM_MD5);
            digest.update(string.getBytes(Charset.forName("UTF-8")));
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Timber.e(e, "MD5 digesting error");
        }
        return null;
    }
}
