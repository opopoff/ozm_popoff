package com.ozm.rocks.ui.gold;

import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.ui.goldFavorite.GoldFavoriteView;
import com.ozm.rocks.ui.goldNovel.GoldNovelView;

import dagger.Component;

@GoldScope
@Component(dependencies = OzomeComponent.class, modules = GoldModule.class)
public interface GoldComponent {
    void inject(GoldActivity activity);
    void inject(GoldView view);
    void inject(GoldFavoriteView view);
    void inject(GoldNovelView view);
}
