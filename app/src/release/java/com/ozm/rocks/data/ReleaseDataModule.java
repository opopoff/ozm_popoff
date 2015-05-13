package com.ozm.rocks.data;

import android.app.Application;
import android.net.Uri;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

import com.ozm.rocks.data.api.OzomeRequestInterceptor;
import com.ozm.rocks.data.api.ReleaseApiModule;
import com.ozm.rocks.ui.ApplicationScope;

import timber.log.Timber;

@Module(includes = {DataModule.class, ReleaseApiModule.class})
public final class ReleaseDataModule {

    @Provides
    @ApplicationScope
    OkHttpClient provideOkHttpClient(Application app, OzomeRequestInterceptor ozomeRequestInterceptor) {
        final OkHttpClient okHttpClient = DataModule.createOkHttpClient(app);
        okHttpClient.interceptors().add(ozomeRequestInterceptor);
        return okHttpClient;
    }

    @Provides
    @ApplicationScope
    Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Timber.e(e, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }
}
