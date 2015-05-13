package com.ozm.rocks.ui.main;

import android.support.annotation.LayoutRes;

import com.ozm.R;

import java.util.Arrays;
import java.util.List;

public enum MainScreens {
    GENERAL_SCREEN(R.layout.main_general_view, R.id.main_general_screen_button),
    EMOTIONS_SCREEN(R.layout.main_emotions_view, R.id.main_emotions_screen_button),
    MY_COLLECTION_SCREEN(R.layout.main_my_collection_view, R.id.main_my_collection_screen_button);

    private final int mResId;
    private final int mButtonId;

    MainScreens(@LayoutRes int resId, int buttonId) {
        this.mResId = resId;
        this.mButtonId = buttonId;
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
}
