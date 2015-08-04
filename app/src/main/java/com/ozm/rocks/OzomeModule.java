package com.ozm.rocks;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module
public final class OzomeModule {
    private final Application application;

    public OzomeModule(Application application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    Application provideApplication() {
        return application;
    }

//    @Provides
//    RefWatcher provideRefWatcher() {
//        return app.getRefWatcher();
//    }
}
