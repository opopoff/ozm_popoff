package com.ozm.rocks.ui.gold;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.ozm.R;
import com.ozm.rocks.ui.main.MainPagerAdapter;

import java.util.Arrays;
import java.util.List;

public enum GoldScreens implements MainPagerAdapter.Item {

    FAVORITE_SCREEN(
            R.layout.gold_favorite_view,
            R.id.main_my_collection_screen_button,
            R.string.gold_tab_favorite_title,
            R.drawable.my_feed_btn_selector),
    NOVEL_SCREEN(
            R.layout.gold_novel_view,
            R.id.main_general_screen_button,
            R.string.gold_tab_novel_title,
            R.drawable.general_feed_btn_selector);

    private final int mResId;
    private final int mButtonId;
    private final int mStringResId;
    private final int mIconSelectorResId;

    GoldScreens(@LayoutRes int resId, int buttonId, @StringRes int stringNameResId, @DrawableRes int iconSelectorId) {
        this.mResId = resId;
        this.mButtonId = buttonId;
        this.mStringResId = stringNameResId;
        this.mIconSelectorResId = iconSelectorId;
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
    public int getButtonId() {
        return mButtonId;
    }

    @Override
    public int getNameResId() {
        return mStringResId;
    }

    @Override
    public int getIconSelectorResId() {
        return mIconSelectorResId;
    }
}
