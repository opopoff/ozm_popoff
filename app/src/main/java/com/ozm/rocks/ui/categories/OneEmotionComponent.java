package com.ozm.rocks.ui.categories;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@OneEmotionScope
@Component(dependencies = OzomeComponent.class, modules = OneEmotionModule.class)
public interface OneEmotionComponent {
    void inject(OneEmotionActivity activity);

    void inject(OneEmotionView view);
}
