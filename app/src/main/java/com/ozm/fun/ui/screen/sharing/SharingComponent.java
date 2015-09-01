package com.ozm.fun.ui.screen.sharing;

import com.ozm.fun.OzomeComponent;

import dagger.Component;

@SharingScope
@Component(dependencies = OzomeComponent.class, modules = SharingModule.class)
public interface SharingComponent {
    void inject(SharingActivity activity);

    void inject(SharingView view);
}
