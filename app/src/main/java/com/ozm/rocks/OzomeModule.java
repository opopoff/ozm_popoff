package com.ozm.rocks;

import android.app.Application;

import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.sharing.ChooseDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PackageManagerTools;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class OzomeModule {
    private final OzomeApplication app;

    public OzomeModule(OzomeApplication app) {
        this.app = app;
    }

    @Provides
    @ApplicationScope
    Application provideApplication() {
        return app;
    }

    @Provides
    @ApplicationScope
    public PackageManagerTools providePackageManagerTools(Application application) {
        return new PackageManagerTools(application);
    }

    @Provides
    @ApplicationScope
    public SharingDialogBuilder provideSharingDialogBuilder() {
        return new SharingDialogBuilder();
    }

    @Provides
    @ApplicationScope
    public ChooseDialogBuilder provideChooseDialogBuilder() {
        return new ChooseDialogBuilder();
    }

    @Provides
    @ApplicationScope
    public NetworkState provideNetworkState(Application application) {
        return new NetworkState(application);
    }

    @Provides
    RefWatcher provideRefWatcher() {
        return app.getRefWatcher();
    }

    @Provides
    @ApplicationScope
    public LikeHideResult provideLikeHideResult() {
        return new LikeHideResult();
    }

}
