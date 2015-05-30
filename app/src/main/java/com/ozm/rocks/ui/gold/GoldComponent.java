package com.ozm.rocks.ui.gold;

import com.ozm.rocks.OzomeComponent;

import dagger.Component;

@GoldScope
@Component(dependencies = OzomeComponent.class, modules = GoldModule.class)
public interface GoldComponent {
    void inject(GoldActivity activity);

    void inject(GoldView view);
}
