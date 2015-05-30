package com.ozm.rocks.ui.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ozm.rocks.base.mvp.BaseView;

public class SettingsView extends LinearLayout implements BaseView {

    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError(Throwable throwable) {

    }
}
