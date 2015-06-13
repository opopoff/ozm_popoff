package com.ozm.rocks.data;

import android.app.Application;
import android.net.Uri;

import com.ozm.rocks.data.api.ReleaseApiModule;
import com.ozm.rocks.ui.ApplicationScope;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module(includes = {DataModule.class, ReleaseApiModule.class})
public final class ReleaseDataModule {

    @Provides
    @ApplicationScope
    OkHttpClient provideOkHttpClient(Application app, OzomeInterceptor ozomeInterceptor) {
        final OkHttpClient client = DataModule.createOkHttpClient(app);
        client.interceptors().add(ozomeInterceptor);
        return client;
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
