package com.umad.rly.ui.screen.settings;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import com.umad.rly.base.mvp.BasePresenter;
import com.umad.rly.data.DataService;
import com.umad.rly.data.SharingService;
import com.umad.rly.data.analytics.LocalyticsController;
import com.umad.rly.data.api.model.Config;
import com.umad.rly.data.api.request.SettingRequest;
import com.umad.rly.ui.ApplicationSwitcher;
import com.umad.rly.ui.screen.main.MainScope;
import com.umad.rly.ui.widget.WidgetController;

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
    private final SharingService sharingService;
    private final Application application;

    private CompositeSubscription subscriptions;

    private Config mConfig;

    @Inject
    public SettingsPresenter(WidgetController widgetController,
                             LocalyticsController localyticsController,
                             ApplicationSwitcher applicationSwitcher,
                             DataService dataService, SharingService sharingService,
                             Application application) {
        this.widgetController = widgetController;
        this.localyticsController = localyticsController;
        this.applicationSwitcher = applicationSwitcher;
        this.dataService = dataService;
        this.sharingService = sharingService;
        this.application = application;
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
                                Timber.d("NewConfig: SettingsView: success from %s", config.from());
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
                                Timber.e(throwable, "SettingPresenter sendCensorShipSetting() method execute error");
                            }
                        }
                );
    }

    public void talkFriend() {
        sharingService.sendFriends()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void openVkGroup() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/club98896965"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }

    public void openTerms() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ozm.io/terms"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }
}
