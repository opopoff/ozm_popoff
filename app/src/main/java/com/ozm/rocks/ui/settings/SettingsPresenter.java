package com.ozm.rocks.ui.settings;

import android.provider.ContactsContract;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.ui.ApplicationSwitcher;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.widget.WidgetController;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@MainScope
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private final WidgetController widgetController;
    private final LocalyticsController localyticsController;
    private final ApplicationSwitcher applicationSwitcher;
    private final DataService dataService;

    @Inject
    public SettingsPresenter(WidgetController widgetController,
                             LocalyticsController localyticsController,
                             ApplicationSwitcher applicationSwitcher,
                             DataService dataService) {
        this.widgetController = widgetController;
        this.localyticsController = localyticsController;
        this.applicationSwitcher = applicationSwitcher;
        this.dataService = dataService;
    }

    public void startService() {
        localyticsController.setWidgetState(LocalyticsController.ON);
        widgetController.start();
    }

    public void stopService() {
        localyticsController.setWidgetState(LocalyticsController.OFF);
        widgetController.stop();
    }

    public void openFeedback() {
        applicationSwitcher.openFeedbackEmailApplication();
    }

    public void openGooglePlay() {
        applicationSwitcher.openGooglePlayAppPage();
    }

    public void deleteAllFromGallery() {
        dataService.deleteAllFromGallery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        ;
    }
}
