package com.umad.wat.ui.screen.start;

import com.umad.wat.OzomeComponent;

import dagger.Component;

@StartScope
@Component(dependencies = OzomeComponent.class)
public interface StartComponent {
    void inject(StartActivity activity);

    void inject(StartView view);
}
