package com.ozm.rocks.ui.start;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreen;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.response.RestRegistration;
import com.ozm.rocks.data.notify.PushWooshActivity;
import com.ozm.rocks.data.prefs.BooleanPreference;
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
//        setTheme(R.style.Theme_Splash);
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
        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;
        private final DataService dataService;
        private final NetworkState networkState;
        private final NoInternetPresenter noInternetPresenter;
        private final TokenStorage tokenStorage;
        private final Application application;
        private CompositeSubscription subscriptions;
        private boolean isRegistered = false;

        @Inject
        public Presenter(ActivityScreenSwitcher screenSwitcher,
                         DataService dataService,
                         SharingService sharingService,
                         NetworkState networkState,
                         NoInternetPresenter noInternetPresenter,
                         TokenStorage tokenStorage,
                         Application application) {
            this.screenSwitcher = screenSwitcher;
            this.dataService = dataService;
            this.sharingService = sharingService;
            this.networkState = networkState;
            this.noInternetPresenter = noInternetPresenter;
            this.tokenStorage = tokenStorage;
            this.application = application;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();
            if (networkState.hasConnection()) {
                call();
            } else {
                networkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
                    @Override
                    public void connectedState(boolean isConnected) {
                        if (isConnected) {
                            networkState.deleteConnectedListener(KEY_LISTENER);
                            noInternetPresenter.hideMessage();
                            call();
                        } else {
                            noInternetPresenter.showMessage();
                        }
                    }
                });
            }
        }

        private void call() {
            if (tokenStorage.isAuthorized()) {
                obtainConfig();
            } else {
                register();
            }
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
            sharingService.sendPackages(tokenStorage.getVkData(), new Action1<Boolean>() {
                @Override
                public void call(Boolean o) {
                    if (o) {
                        openNextScreen();
                    } else {
                        if (!isRegistered) {
                            isRegistered = true;
                            register();
                        } else {
                            Toast.makeText(application, application.getString(
                                    R.string.start_screen_authorization_error), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }

        public void openNextScreen() {
            if (!tokenStorage.isOnBoardingShowed()) {
                tokenStorage.setOnBoardingShowed();
                screenSwitcher.open(new InstructionActivity.Screen());
            } else {
                screenSwitcher.open(new MainActivity.Screen());
            }
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
