package com.ozm.rocks.util;

import android.net.Uri;

public class UrlFormat {
    public static Uri getImageUri(String url) {
        return Uri.parse(url);
    }
}
