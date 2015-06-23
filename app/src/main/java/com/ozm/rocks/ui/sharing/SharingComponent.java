package com.ozm.rocks.ui.sharing;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@SharingScope
@Component(dependencies = OzomeComponent.class, modules = SharingModule.class)
public interface SharingComponent {
    void inject(SharingActivity activity);

    void inject(SharingView view);
}
