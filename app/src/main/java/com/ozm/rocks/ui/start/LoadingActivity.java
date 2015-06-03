package com.ozm.rocks.ui.start;

import android.os.Bundle;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.ui.widget.WidgetService;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class LoadingActivity extends BaseActivity implements HasComponent<LoadingComponent> {

    @Inject
    Presenter presenter;

    @Inject
    TokenStorage tokenStorage;

    private LoadingComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_U2020);
        super.onCreate(savedInstanceState);
        // Start WidgetService if it's a first start of application;
        final boolean isFirstStart = tokenStorage.isFirstStart();
        final boolean isShowWidget = tokenStorage.isShowWidget();
        final boolean isWidgetStarted = WidgetService.isServiceRunning(this);
        if (isFirstStart && isShowWidget && !isWidgetStarted) {
            WidgetService.startService(this);
        }
        if (isFirstStart) {
            tokenStorage.updateFirstStart();
        }
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
        return R.layout.start_loading_layout;
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

        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;
        private final DataService dataService;
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(ActivityScreenSwitcher screenSwitcher,
                         DataService dataService,
                         SharingService sharingService) {
            this.screenSwitcher = screenSwitcher;
            this.dataService = dataService;
            this.sharingService = sharingService;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();

            sharingService.sendPackages(new Action1<Boolean>() {
                @Override
                public void call(Boolean o) {
                    if (o) {
                        openMainScreen();
                    } else {
                        LoadingView view = getView();
                        if (view != null) {
                            view.mNoInternetView.setVisibility(View.VISIBLE);
                        }
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
            super.onDestroy();
        }

        public void openMainScreen() {
            screenSwitcher.open(new MainActivity.Screen());
        }
    }
}
