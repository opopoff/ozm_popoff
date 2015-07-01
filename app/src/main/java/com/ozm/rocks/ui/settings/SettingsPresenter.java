package com.ozm.rocks.ui.settings;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.SettingRequest;
import com.ozm.rocks.ui.ApplicationSwitcher;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.widget.WidgetController;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@MainScope
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private final WidgetController widgetController;
    private final LocalyticsController localyticsController;
    private final ApplicationSwitcher applicationSwitcher;
    private final DataService dataService;

    private CompositeSubscription subscriptions;

    private Config mConfig;

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

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
        loadConfig();
    }

    @Override
    protected void onDestroy() {
        subscriptions.unsubscribe();
        super.onDestroy();
    }

    private void loadConfig() {
        if (mConfig != null) {
            bindConfigData();
            return;
        }

        subscriptions.add(dataService.getConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Config>() {
                            @Override
                            public void call(Config config) {
                                mConfig = config;
                                bindConfigData();
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.w(throwable, "SettingPresenter loadConfig() method execute error");
                            }
                        }
                ));
    }

    private void bindConfigData() {
        final SettingsView view = getView();
        if (view == null) return;
        view.bindConfigData(mConfig);
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
    }

    public void sendCensorShipSetting(boolean checked) {
        dataService.sendCensorshipSetting(new SettingRequest(checked))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Timber.d("sendCensorShipSetting() result: %s", s);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.w(throwable, "SettingPresenter sendCensorShipSetting() method execute error");
                            }
                        }
                );
    }
}
