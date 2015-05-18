package com.ozm.rocks;

import android.app.Application;

import com.novoda.merlin.Merlin;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.util.PackageManagerTools;

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
    public Merlin provideMerlin(Application application) {
        return new Merlin.Builder().withConnectableCallbacks().build(application);
    }
}
