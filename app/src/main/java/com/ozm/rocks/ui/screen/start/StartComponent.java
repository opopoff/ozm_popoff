package com.ozm.rocks.ui.screen.start;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@StartScope
@Component(dependencies = OzomeComponent.class)
public interface StartComponent {
    void inject(StartActivity activity);

    void inject(StartView view);
}
