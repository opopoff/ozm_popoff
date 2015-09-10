package com.umad.wat.ui.screen.sharing;

import com.umad.wat.OzomeComponent;

import dagger.Component;

@SharingScope
@Component(dependencies = OzomeComponent.class, modules = SharingModule.class)
public interface SharingComponent {
    void inject(SharingActivity activity);

    void inject(SharingView view);
}
