package com.ozm.rocks.ui.main;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.ozm.R;

import java.util.Arrays;
import java.util.List;

public enum MainScreens {
    EMOTIONS_SCREEN(R.layout.emotions_view, R.id.main_emotions_screen_button, R.string.emotions_feed_name,
            R.drawable.emotions_feed_btn_selector),
    GENERAL_SCREEN(R.layout.general_view, R.id.main_general_screen_button, R.string.general_feed_name,
            R.drawable.general_feed_btn_selector),
    FAVORITE_SCREEN(R.layout.personal_view, R.id.main_my_collection_screen_button, R.string
            .my_feed_name,
            R.drawable.my_feed_btn_selector);

    private final int mResId;
    private final int mButtonId;
    private final int mStringResId;
    private final int mIconSelectorResId;

    MainScreens(@LayoutRes int resId, int buttonId, @StringRes int stringNameResId, @DrawableRes int iconSelectorId) {
        this.mResId = resId;
        this.mButtonId = buttonId;
        this.mStringResId = stringNameResId;
        this.mIconSelectorResId = iconSelectorId;
    }

    public int getResId() {
        return mResId;
    }

    public static List<MainScreens> getList() {
        return Arrays.asList(MainScreens.values());
    }

    public int getButtonId() {
        return mButtonId;
    }

    public int getNameResId() {
        return mStringResId;
    }

    public int getIconSelectorResId() {
        return mIconSelectorResId;
    }
}
