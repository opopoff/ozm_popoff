package com.ozm.rocks.ui.settings;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.widget.WidgetController;

import javax.inject.Inject;

@MainScope
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private final WidgetController widgetController;

    @Inject
    public SettingsPresenter(WidgetController widgetController) {
        this.widgetController = widgetController;
    }

    public void startService() {
        widgetController.start();
    }

    public void stopService() {
        widgetController.stop();
    }
}
