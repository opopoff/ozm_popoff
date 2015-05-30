package com.ozm.rocks.ui.settings;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.ui.main.MainScope;

import javax.inject.Inject;

@MainScope
public class SettingsPresenter extends BasePresenter<SettingsView> {

    @Inject
    public SettingsPresenter() {
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
