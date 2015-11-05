package com.umad.wat.ui.screen.start;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.umad.R;
import com.umad.wat.OzomeComponent;
import com.umad.wat.base.HasComponent;
import com.umad.wat.base.mvp.BasePresenter;
import com.umad.wat.base.mvp.BaseView;
import com.umad.wat.base.navigation.activity.ActivityScreen;
import com.umad.wat.base.navigation.activity.ActivityScreenSwitcher;
import com.umad.wat.base.tools.ToastPresenter;
import com.umad.wat.data.DataService;
import com.umad.wat.data.SharingService;
import com.umad.wat.data.TokenStorage;
import com.umad.wat.data.analytics.LocalyticsController;
import com.umad.wat.data.api.ServerErrorException;
import com.umad.wat.data.api.model.Config;
import com.umad.wat.data.notify.PushWooshActivity;
import com.umad.wat.data.prefs.BooleanPreference;
import com.umad.wat.ui.message.NoInternetPresenter;
import com.umad.wat.ui.screen.main.MainActivity;
import com.umad.wat.ui.widget.WidgetController;
import com.umad.wat.util.NetworkState;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class StartActivity extends PushWooshActivity implements HasComponent<StartComponent> {

    public static final String WP_OPEN_FROM_WIDGET = "StartActivity.widget";
    private static final String SP_NAME = "ozome";
    private static final String SP_ON_BOARDING = "SharedPreference.onBoarding";

    @Inject
    Presenter presenter;

    @Inject
    WidgetController widgetController;

    @Inject
    LocalyticsController localyticsController;

    private StartComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        if (!new BooleanPreference(sharedPreferences, SP_ON_BOARDING, false).get()) {
            setTheme(R.style.Theme_Splash);
        }
        super.onCreate(savedInstanceState);
        // Start WidgetService if it's a first start of application;
        widgetController.checkOnRunning();
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(WP_OPEN_FROM_WIDGET)) {
            localyticsController.openApp(LocalyticsController.WIDGET);
        }
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerStartComponent.builder().
                ozomeComponent(ozomeComponent).build();
        component.inject(this);
    }

    @Override
    protected void onDestroy() {
        component = null;
        super.onDestroy();
    }

    @Override
    protected int layoutId() {
        return R.layout.start_layout;
    }

    @Override
    protected BasePresenter<? extends BaseView> presenter() {
        return presenter;
    }

    @Override
    protected int viewId() {
        return R.id.loading_view;
    }

    @Override
    public StartComponent getComponent() {
        return component;
    }

    @StartScope
    public static final class Presenter extends BasePresenter<StartView> {
        private static final String KEY_LISTENER = "InstructionActivity.Presenter";
        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;
        private final DataService dataService;
        private final NetworkState networkState;
        private final NoInternetPresenter noInternetPresenter;
        private final TokenStorage tokenStorage;
        private final Application application;
        private final ToastPresenter toastPresenter;
        private CompositeSubscription subscriptions;

        private Config mConfig;

        @Inject
        public Presenter(ActivityScreenSwitcher screenSwitcher,
                         DataService dataService,
                         SharingService sharingService,
                         NetworkState networkState,
                         NoInternetPresenter noInternetPresenter,
                         TokenStorage tokenStorage,
                         Application application,
                         ToastPresenter toastPresenter) {
            this.screenSwitcher = screenSwitcher;
            this.dataService = dataService;
            this.sharingService = sharingService;
            this.networkState = networkState;
            this.noInternetPresenter = noInternetPresenter;
            this.tokenStorage = tokenStorage;
            this.application = application;
            this.toastPresenter = toastPresenter;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();
            if (networkState.hasConnection()) {
                obtainConfig();
            } else {
                noInternetPresenter.showMessage();
                networkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
                    @Override
                    public void connectedState(boolean isConnected) {
                        if (isConnected) {
                            networkState.deleteConnectedListener(KEY_LISTENER);
                            noInternetPresenter.hideMessage();
                            obtainConfig();
                        } else {
                            noInternetPresenter.showMessage();
                        }
                    }
                });
            }
        }

        public void obtainConfig() {

            final Subscription subscribe = dataService.getConfig()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<Config>() {
                                @Override
                                public void call(Config config) {
                                    if (config == null) {
                                        return;
                                    }
                                    if (mConfig == null) {
                                        mConfig = config;
                                        openNextScreen();
                                    }
                                }
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (throwable instanceof DataService.EmptyConfigThrowable) {
                                        return;
                                    }
                                    if (throwable instanceof ServerErrorException) {
                                        ServerErrorException serverErrorException = (ServerErrorException) throwable;
                                        final int errorCode = serverErrorException.getErrorCode();
                                        if (errorCode == ServerErrorException.ERROR_TOKEN_EXPIRED ||
                                                errorCode == ServerErrorException.ERROR_TOKEN_INVALID) {
                                            Toast.makeText(application, application.getString(
                                                            R.string.start_screen_authorization_error),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                    );
            subscriptions.add(subscribe);
        }

        public void openNextScreen() {
//            if (!tokenStorage.isOnBoardingShowed()) {
//                tokenStorage.setOnBoardingShowed();
//                screenSwitcher.open(new InstructionActivity.Screen());

//            } else {
            screenSwitcher.open(new MainActivity.Screen());
//            }
        }

        @Override
        protected void onDestroy() {
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
            sharingService.unsubscribe();
            networkState.deleteConnectedListener(KEY_LISTENER);
            super.onDestroy();
        }
    }

    public static final class Screen extends ActivityScreen {
        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        @Override
        protected Class<? extends Activity> activityClass() {
            return StartActivity.class;
        }
    }

}
