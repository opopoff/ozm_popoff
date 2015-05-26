package com.ozm.rocks.ui.categories;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class OneEmotionModule {
    public final long categoryId;
    public final String categoryName;

    public OneEmotionModule(long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    @Provides
    @OneEmotionScope
    @Named("category")
    long provideCategoryId() {
        return categoryId;
    }

    @Provides
    @OneEmotionScope
    @Named("categoryName")
    String provideCategoryName() {
        return categoryName;
    }
}
