package com.umad.rly.data;

import android.app.Application;

import com.koushikdutta.ion.Ion;
import com.umad.rly.ApplicationScope;
import com.umad.rly.data.api.ApiModule;
import com.umad.rly.data.prefs.SharedPreferenceModule;
import com.umad.rly.data.prefs.rating.RatingPreferenceModule;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import dagger.Module;
import dagger.Provides;

import static java.util.concurrent.TimeUnit.SECONDS;

@Module(includes = { ApiModule.class, SharedPreferenceModule.class, RatingPreferenceModule.class})
public final class DataModule {
    static final int DISK_CACHE_SIZE = 200 * 1024 * 1024; // 200MB

    @Provides
    @ApplicationScope
    Clock provideClock() {
        return Clock.REAL;
    }

    @Provides
    @ApplicationScope
    Ion provideIon(Application application) {
        return Ion.getInstance(application.getApplicationContext(), "OzomeIon");
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        return client;
    }
}
