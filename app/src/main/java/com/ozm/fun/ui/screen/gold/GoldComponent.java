package com.ozm.fun.ui.screen.gold;

import com.ozm.fun.OzomeComponent;
import com.ozm.fun.ui.screen.gold.favorite.GoldFavoriteView;
import com.ozm.fun.ui.screen.gold.novel.GoldNovelView;

import dagger.Component;

@GoldScope
@Component(dependencies = OzomeComponent.class, modules = GoldModule.class)
public interface GoldComponent {
    void inject(GoldActivity activity);
    void inject(GoldView view);
    void inject(GoldFavoriteView view);
    void inject(GoldNovelView view);
}
