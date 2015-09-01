package com.ozm.fun.ui.screen.start;

import com.ozm.fun.OzomeComponent;

import dagger.Component;

@StartScope
@Component(dependencies = OzomeComponent.class)
public interface StartComponent {
    void inject(StartActivity activity);

    void inject(StartView view);
}
