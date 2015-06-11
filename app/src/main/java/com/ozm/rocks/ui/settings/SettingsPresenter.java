package com.ozm.rocks.ui.settings;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.widget.WidgetController;

import javax.inject.Inject;

@MainScope
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private final WidgetController widgetController;
    private final LocalyticsController localyticsController;

    @Inject
    public SettingsPresenter(WidgetController widgetController, LocalyticsController localyticsController) {
        this.widgetController = widgetController;
        this.localyticsController = localyticsController;
    }

    public void startService() {
        localyticsController.setWidgetState(LocalyticsController.ON);
        widgetController.start();
    }

    public void stopService() {
        localyticsController.setWidgetState(LocalyticsController.OFF);
        widgetController.stop();
    }
}
