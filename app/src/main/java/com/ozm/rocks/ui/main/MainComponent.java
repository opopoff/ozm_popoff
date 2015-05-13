package com.ozm.rocks.ui.main;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@MainScope
@Component(dependencies = OzomeComponent.class)
public interface MainComponent {
    void inject(MainActivity activity);

    void inject(MainView view);

    void inject(MainEmotionsView view);

    void inject(MainGeneralView view);

    void inject(MainMyCollectionView view);
}
