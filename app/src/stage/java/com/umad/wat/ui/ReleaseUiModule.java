package com.umad.wat.ui;

import dagger.Module;
import dagger.Provides;

import com.umad.wat.ApplicationScope;
import com.umad.wat.base.mvp.Registry;
import com.umad.wat.ui.annotation.ActivityScreenSwitcherServer;

@Module(includes = UiModule.class)
public class ReleaseUiModule {
    @Provides
    @ApplicationScope
    AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }

    @Provides
    @ApplicationScope
    ActivityHierarchyServer provideActivityHierarchyServer(
            @ActivityScreenSwitcherServer ActivityHierarchyServer server) {
        final ActivityHierarchyServer.Proxy proxy = new ActivityHierarchyServer.Proxy();
        proxy.addServer(server);
        proxy.addServer(Registry.SERVER);
        return proxy;
    }
}
