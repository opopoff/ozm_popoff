package com.ozm.rocks.ui.start;

import android.os.Bundle;
import android.os.Handler;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainView;
import com.ozm.rocks.ui.sharing.SharingService;

import javax.inject.Inject;

public class LoadingActivity extends BaseActivity implements HasComponent<LoadingComponent> {
    @Inject
    Presenter presenter;

    private LoadingComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_U2020);
        super.onCreate(savedInstanceState);
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
    public static final class Presenter extends BasePresenter<MainView> {

        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;

        @Inject
        public Presenter(ActivityScreenSwitcher screenSwitcher, SharingService sharingService) {
            this.screenSwitcher = screenSwitcher;
            this.sharingService = sharingService;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            sharingService.sendPackages();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMainScreen();
                }
            }, 5000);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

        public void openMainScreen() {
            screenSwitcher.open(new MainActivity.Screen());
        }
    }
}
