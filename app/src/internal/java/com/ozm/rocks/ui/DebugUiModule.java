package com.ozm.rocks.ui;

import com.ozm.rocks.ApplicationScope;
import com.ozm.rocks.base.mvp.Registry;
import com.ozm.rocks.ui.annotation.ActivityScreenSwitcherServer;
import com.ozm.rocks.ui.debug.DebugAppContainer;
import com.ozm.rocks.ui.debug.SocketActivityHierarchyServer;

import dagger.Module;
import dagger.Provides;

@Module(includes = UiModule.class)
public class DebugUiModule {
    @Provides
    @ApplicationScope
    AppContainer provideAppContainer(DebugAppContainer appContainer) {
        return appContainer;
    }

    @Provides
    @ApplicationScope
    ActivityHierarchyServer provideActivityHierarchyServer(
            @ActivityScreenSwitcherServer ActivityHierarchyServer server) {
        final ActivityHierarchyServer.Proxy proxy = new ActivityHierarchyServer.Proxy();
        proxy.addServer(server);
        proxy.addServer(Registry.SERVER);
        proxy.addServer(new SocketActivityHierarchyServer());
        return proxy;
    }
}
