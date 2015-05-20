package com.ozm.rocks.ui.oneEmotionList;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class OneEmotionModule {
    public final long categoryId;

    public OneEmotionModule(long categoryId) {
        this.categoryId = categoryId;
    }

    @Provides
    @OneEmotionScope
    @Named("category")
    long provideCategoryId() {
        return categoryId;
    }
}
