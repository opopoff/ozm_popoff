package com.ozm.rocks.ui.gold;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class GoldModule {
    public final long categoryId;
    public final String categoryName;

    public GoldModule(long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    @Provides
    @GoldScope
    @Named("category")
    long provideCategoryId() {
        return categoryId;
    }

    @Provides
    @GoldScope
    @Named("categoryName")
    String provideCategoryName() {
        return categoryName;
    }
}
