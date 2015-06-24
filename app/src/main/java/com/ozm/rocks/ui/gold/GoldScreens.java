package com.ozm.rocks.ui.gold;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.ozm.R;
import com.ozm.rocks.ui.main.MainPagerAdapter;

import java.util.Arrays;
import java.util.List;

public enum GoldScreens implements MainPagerAdapter.Item {

    FAVORITE_SCREEN(
            R.layout.gold_favorite_view,
            R.string.gold_tab_favorite_title),
    NOVEL_SCREEN(
            R.layout.gold_novel_view,
            R.string.gold_tab_novel_title);

    private final int mResId;
    private final int mStringResId;

    GoldScreens(@LayoutRes int resId, @StringRes int stringNameResId) {
        this.mResId = resId;
        this.mStringResId = stringNameResId;
    }

    @Override
    public int getResId() {
        return mResId;
    }

    public static List<MainPagerAdapter.Item> getList() {
        final MainPagerAdapter.Item[] values = GoldScreens.values();
        return Arrays.asList(values);
    }

    @Override
    public int getNameResId() {
        return mStringResId;
    }
}
