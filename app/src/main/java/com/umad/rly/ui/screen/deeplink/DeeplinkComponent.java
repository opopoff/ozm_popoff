package com.umad.rly.ui.screen.deeplink;

import com.umad.rly.OzomeComponent;

import dagger.Component;

@DeeplinkScope
@Component(dependencies = OzomeComponent.class)
public interface DeeplinkComponent {
    void inject(DeeplinkActivity activity);
    void inject(DeepllinkView view);
}
