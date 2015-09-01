package com.ozm.fun.ui.screen.deeplink;

import com.ozm.fun.OzomeComponent;

import dagger.Component;

@DeeplinkScope
@Component(dependencies = OzomeComponent.class)
public interface DeeplinkComponent {
    void inject(DeeplinkActivity activity);
    void inject(DeepllinkView view);
}
