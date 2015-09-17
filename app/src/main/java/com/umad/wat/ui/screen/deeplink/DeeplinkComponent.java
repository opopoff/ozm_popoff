package com.umad.wat.ui.screen.deeplink;

import com.umad.wat.OzomeComponent;

import dagger.Component;

@DeeplinkScope
@Component(dependencies = OzomeComponent.class)
public interface DeeplinkComponent {
    void inject(DeeplinkActivity activity);
    void inject(DeepllinkView view);
}
