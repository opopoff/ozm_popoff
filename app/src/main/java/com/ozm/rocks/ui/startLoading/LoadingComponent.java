package com.ozm.rocks.ui.startLoading;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@LoadingScope
@Component(dependencies = OzomeComponent.class)
public interface LoadingComponent {
    void inject(LoadingActivity activity);

    void inject(LoadingView view);
}
