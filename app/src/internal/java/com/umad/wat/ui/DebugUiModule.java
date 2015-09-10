package com.umad.wat.ui;

import com.umad.wat.ApplicationScope;
import com.umad.wat.base.mvp.Registry;
import com.umad.wat.ui.annotation.ActivityScreenSwitcherServer;
import com.umad.wat.ui.debug.DebugAppContainer;
import com.umad.wat.ui.debug.SocketActivityHierarchyServer;

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
