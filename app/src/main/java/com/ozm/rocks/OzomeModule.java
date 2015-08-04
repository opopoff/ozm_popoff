package com.ozm.rocks;

import android.app.Application;

import com.ozm.rocks.ui.screen.categories.LikeHideResult;

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

    @Provides
    @ApplicationScope
    public LikeHideResult provideLikeHideResult() {
        return new LikeHideResult();
    }

}
