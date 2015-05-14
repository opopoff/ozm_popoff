package com.ozm.rocks.util;

import android.net.Uri;

import com.ozm.rocks.data.api.ApiModule;

public class UrlFormat {
    public static Uri getImageUri(String url) {
        return Uri.parse(ApiModule.PRODUCTION_API_URL + url);
    }
}
