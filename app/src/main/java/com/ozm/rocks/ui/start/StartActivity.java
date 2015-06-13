package com.ozm.rocks.ui.start;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.api.response.RestRegistration;
import com.ozm.rocks.data.notify.PushWooshActivity;
import com.ozm.rocks.ui.instruction.InstructionActivity;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.message.NoInternetPresenter;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.ui.widget.WidgetController;
import com.ozm.rocks.util.NetworkState;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class StartActivity extends PushWooshActivity implements HasComponent<StartComponent> {

    @Inject
    Presenter presenter;

    @Inject
    WidgetController widgetController;

    private StartComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_U2020);
        super.onCreate(savedInstanceState);
        // Start WidgetService if it's a first start of application;
        widgetController.checkOnRunning();
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
        return R.layout.loading_layout;
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
        private static final String SP_START = "StartActivity.SP.Start";
        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;
        private final DataService dataService;
        private final NetworkState networkState;
        private final NoInternetPresenter noInternetPresenter;
        private final SharedPreferences sharedPreferences;
        private final TokenStorage tokenStorage;
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(ActivityScreenSwitcher screenSwitcher,
                         DataService dataService,
                         SharingService sharingService,
                         NetworkState networkState,
                         NoInternetPresenter noInternetPresenter,
                         Application application,
                         TokenStorage tokenStorage) {
            this.screenSwitcher = screenSwitcher;
            this.dataService = dataService;
            this.sharingService = sharingService;
            this.networkState = networkState;
            this.noInternetPresenter = noInternetPresenter;
            this.tokenStorage = tokenStorage;
            sharedPreferences = application.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();
            networkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
                @Override
                public void connectedState(boolean isConnected) {
                    if (isConnected) {
                        networkState.deleteConnectedListener(KEY_LISTENER);
                        noInternetPresenter.hideMessage();
                        if (tokenStorage.isAuthorized()) {
                            obtainConfig();
                        } else {
                            register();
                        }
                    } else {
                        noInternetPresenter.showMessage();
                    }
                }
            });
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

        public void register() {
            dataService.register()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<RestRegistration>() {
                                @Override
                                public void call(RestRegistration restRegistration) {
                                    Timber.d("Registration: success");
                                    tokenStorage.putUserKey(restRegistration.key);
                                    tokenStorage.putUserSecret(restRegistration.secret);
                                    obtainConfig();
                                }
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.d(throwable, "Registration: error");
                                }
                            });
        }

        public void obtainConfig() {
            sharingService.sendPackages(new Action1<Boolean>() {
                @Override
                public void call(Boolean o) {
                    openNextScreen();
                }
            });
        }

        public void openNextScreen() {
            boolean isFirst = sharedPreferences.getBoolean(SP_START, true);
            if (isFirst) {
                sharedPreferences.edit().putBoolean(SP_START, false).apply();
                screenSwitcher.open(new InstructionActivity.Screen());
            } else {
                screenSwitcher.open(new MainActivity.Screen());
            }
        }
    }
}
