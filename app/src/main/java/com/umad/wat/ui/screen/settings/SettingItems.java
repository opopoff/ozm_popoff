package com.umad.wat.ui.screen.settings;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.umad.R;

enum SettingItems {
    WIDGET(R.id.setting_item_widget,
            R.string.settings_widget_title, R.drawable.ic_menu_widget, true),
    CENSORSHIP(R.id.setting_item_censorship,
            R.string.settings_censorship_title, R.drawable.ic_menu_censorship, true),
    ALBUM(R.id.setting_item_create_album,
            R.string.settings_album_title, R.drawable.ic_menu_album, true),
    FEEDBACK(R.id.setting_item_feedback,
            R.string.settings_feedback_title, R.drawable.ic_menu_feedback, false),
    ESTIMATION(R.id.setting_item_estimation,
            R.string.settings_estimate_title, R.drawable.ic_menu_estimation, false),
    TALK_FRIEND(R.id.setting_item_talk_friend,
            R.string.settings_talk_friend_title, R.drawable.ic_menu_talk_friends, false),
    VK_GROUP(R.id.setting_item_vk_group,
            R.string.settings_vk_group_title, R.drawable.ic_menu_vk_group, false),
    TERMS(R.id.setting_item_terms,
            R.string.settings_terms_title, R.drawable.ic_widget, false);
    private final int viewId;
    private final int titleResId;
    private final int iconResId;
    private final boolean isCheckable;

    SettingItems(int viewId, @StringRes int titleResId, @DrawableRes int iconResId, boolean isCheckable) {
        this.viewId = viewId;
        this.titleResId = titleResId;
        this.iconResId = iconResId;
        this.isCheckable = isCheckable;
    }

    public int getViewId() {
        return viewId;
    }

    public String getTitle(Context context) {
        return context.getResources().getString(titleResId);
    }

    public int getTitleResId() {
        return titleResId;
    }

    public boolean isCheckable() {
        return isCheckable;
    }

    public int getIconResId() {
        return iconResId;
    }
}
