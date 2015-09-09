package com.umad.rly.ui.screen.start;

import com.umad.rly.OzomeComponent;

import dagger.Component;

@StartScope
@Component(dependencies = OzomeComponent.class)
public interface StartComponent {
    void inject(StartActivity activity);

    void inject(StartView view);
}
