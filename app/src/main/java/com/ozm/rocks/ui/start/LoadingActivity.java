package com.ozm.rocks.ui.start;

import android.os.Bundle;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.message.NoInternetPresenter;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.ui.widget.WidgetController;
import com.ozm.rocks.util.NetworkState;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class LoadingActivity extends BaseActivity implements HasComponent<LoadingComponent> {

    @Inject
    Presenter presenter;

    @Inject
    WidgetController widgetController;

    private LoadingComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_U2020);
        super.onCreate(savedInstanceState);
        // Start WidgetService if it's a first start of application;
        widgetController.checkOnRunning();
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerLoadingComponent.builder().
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
    public LoadingComponent getComponent() {
        return component;
    }

    @LoadingScope
    public static final class Presenter extends BasePresenter<LoadingView> {
        private static final String KEY_LISTENER = "LoadingActivity.Presenter";
        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;
        private final DataService dataService;
        private CompositeSubscription subscriptions;
        private NetworkState networkState;
        private NoInternetPresenter noInternetPresenter;

        @Inject
        public Presenter(ActivityScreenSwitcher screenSwitcher,
                         DataService dataService,
                         SharingService sharingService,
                         NetworkState networkState,
                         NoInternetPresenter noInternetPresenter) {
            this.screenSwitcher = screenSwitcher;
            this.dataService = dataService;
            this.sharingService = sharingService;
            this.networkState = networkState;
            this.noInternetPresenter = noInternetPresenter;
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
                        sharingService.sendPackages(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean o) {
                                openMainScreen();
                            }
                        });
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

        public void openMainScreen() {
            screenSwitcher.open(new MainActivity.Screen());
        }
    }
}
