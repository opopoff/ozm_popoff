package com.ozm.rocks.ui.screen.deeplink;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@DeeplinkScope
@Component(dependencies = OzomeComponent.class)
public interface DeeplinkComponent {
    void inject(DeeplinkActivity activity);
    void inject(DeepllinkView view);
}
