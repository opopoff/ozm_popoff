package com.ozm.fun.ui.screen.main;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.ozm.R;
import com.ozm.fun.ui.misc.CoordinatorPageAdapter;

import java.util.Arrays;
import java.util.List;

public enum MainScreens implements CoordinatorPageAdapter.Item {
    EMOTIONS_SCREEN(
            R.layout.main_emotions_view,
            R.string.emotions_feed_name),
    FAVORITE_SCREEN(
            R.layout.main_personal_view,
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

    public static List<CoordinatorPageAdapter.Item> getList() {
        final CoordinatorPageAdapter.Item[] values = MainScreens.values();
        return Arrays.asList(values);
    }

    @Override
    public int getNameResId() {
        return mStringResId;
    }
}
