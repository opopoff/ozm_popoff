package com.umad.rly.ui;

import android.app.Activity;

import com.umad.rly.ApplicationScope;
import com.umad.rly.base.navigation.activity.ActivityScreenSwitcher;
import com.umad.rly.base.tools.KeyboardPresenter;
import com.umad.rly.ui.annotation.ActivityScreenSwitcherServer;

import dagger.Module;
import dagger.Provides;

@Module
public class UiModule {

    @Provides
    @ApplicationScope
    ActivityScreenSwitcher provideActivityScreenSwitcher() {
        return new ActivityScreenSwitcher();
    }

    @Provides
    @ApplicationScope
    @ActivityScreenSwitcherServer
    ActivityHierarchyServer provideActivityScreenSwitcherServer(final ActivityScreenSwitcher screenSwitcher,
                                                                final KeyboardPresenter keyboardPresenter) {
        return new ActivityHierarchyServer.Empty() {
            @Override
            public void onActivityStarted(Activity activity) {
                screenSwitcher.attach(activity);
                keyboardPresenter.attach(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                screenSwitcher.detach(activity);
                keyboardPresenter.detach(activity);
            }
        };
    }
}
