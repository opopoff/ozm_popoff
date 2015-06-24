package com.ozm.rocks.ui.main;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.ozm.R;

import java.util.Arrays;
import java.util.List;

public enum MainScreens implements MainPagerAdapter.Item {
    EMOTIONS_SCREEN(
            R.layout.emotions_view,
            R.string.emotions_feed_name),
//    GENERAL_SCREEN(
//            R.layout.general_view,
//            R.string.general_feed_name),
    FAVORITE_SCREEN(
            R.layout.personal_view,
            R.string
            .my_feed_name);

    private final int mResId;
    private final int mStringResId;

    MainScreens(@LayoutRes int resId, @StringRes int stringNameResId) {
        this.mResId = resId;
        this.mStringResId = stringNameResId;
    }

    @Override
    public int getResId() {
        return mResId;
    }

    public static List<MainPagerAdapter.Item> getList() {
        final MainPagerAdapter.Item[] values = MainScreens.values();
        return Arrays.asList(values);
    }

    @Override
    public int getNameResId() {
        return mStringResId;
    }
}
