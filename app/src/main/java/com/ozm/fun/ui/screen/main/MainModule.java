package com.ozm.fun.ui.screen.main;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    private final MainActivity activity;

    public MainModule(MainActivity activity) {
        this.activity = activity;
    }

    @Provides
    @MainScope
    public MainActivity provideActivity() {
        return activity;
    }
}
