package com.ozm.fun.ui;

import com.ozm.fun.ApplicationScope;
import com.ozm.fun.base.mvp.Registry;
import com.ozm.fun.ui.annotation.ActivityScreenSwitcherServer;
import com.ozm.fun.ui.debug.DebugAppContainer;
import com.ozm.fun.ui.debug.SocketActivityHierarchyServer;

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
