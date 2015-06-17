package com.ozm.rocks.ui.gold;

import com.ozm.rocks.data.api.response.Category;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class GoldModule {
    public final Category category;
    public final boolean isFirst;

    public GoldModule(Category category, boolean isFirst) {
        this.category = category;
        this.isFirst = isFirst;
    }

    @Provides
    @GoldScope
    @Named("category")
    Category provideCategoryId() {
        return category;
    }

    @Provides
    @GoldScope
    @Named("isFirst")
    boolean provideIsFirst() {
        return isFirst;
    }

}
