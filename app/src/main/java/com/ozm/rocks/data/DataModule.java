package com.ozm.rocks.data;

import android.app.Application;
import android.content.SharedPreferences;

import com.ozm.rocks.data.api.ApiModule;
import com.ozm.rocks.ui.ApplicationScope;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;
import static java.util.concurrent.TimeUnit.SECONDS;

@Module(includes = ApiModule.class)
public final class DataModule {
    static final int DISK_CACHE_SIZE = 200 * 1024 * 1024; // 200MB

    @Provides
    @ApplicationScope
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("Ozome", MODE_PRIVATE);
    }

    @Provides
    @ApplicationScope
    Clock provideClock() {
        return Clock.REAL;
    }

    @Provides
    @ApplicationScope
    TokenStorage provideTokenStorage(Application application) {
        return new HawkTokenStorage(application);
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
