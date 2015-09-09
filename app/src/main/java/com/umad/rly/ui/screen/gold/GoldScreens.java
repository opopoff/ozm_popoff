package com.umad.rly.ui.screen.gold;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.ozm.R;
import com.umad.rly.ui.misc.CoordinatorPageAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public enum GoldScreens implements CoordinatorPageAdapter.Item {

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

    public static List<CoordinatorPageAdapter.Item> getList() {
        final CoordinatorPageAdapter.Item[] values = GoldScreens.values();
        return new LinkedList(Arrays.asList(values));
    }

    @Override
    public int getNameResId() {
        return mStringResId;
    }
}
