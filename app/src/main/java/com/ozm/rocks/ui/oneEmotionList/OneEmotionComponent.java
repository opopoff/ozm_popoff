package com.ozm.rocks.ui.oneEmotionList;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@OneEmotionScope
@Component(dependencies = OzomeComponent.class)
public interface OneEmotionComponent {
    void inject(OneEmotionActivity activity);

    void inject(OneEmotionView view);
}
