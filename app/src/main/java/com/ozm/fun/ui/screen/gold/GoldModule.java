package com.ozm.fun.ui.screen.gold;

import com.ozm.fun.data.api.response.Category;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class GoldModule {

    public static final String CATEGORY = "category";
    public static final String ISFIRST = "isFirst";

    public final Category category;
    public final boolean isFirst;

    public GoldModule(Category category, boolean isFirst) {
        this.category = category;
        this.isFirst = isFirst;
    }

    @Provides
    @GoldScope
    @Named(CATEGORY)
    Category provideCategoryId() {
        return category;
    }

    @Provides
    @GoldScope
    @Named(ISFIRST)
    boolean provideIsFirst() {
        return isFirst;
    }

}
