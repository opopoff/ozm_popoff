package com.ozm.rocks.ui.start;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainView;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

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

        private final DataService dataService;
        private final TokenStorage tokenStorage;
        private final ActivityScreenSwitcher screenSwitcher;
        private final KeyboardPresenter keyboardPresenter;
        private final PackageManagerTools mPackageManagerTools;
        private ArrayList<PInfo> mPackages;
        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService, TokenStorage tokenStorage,
                         ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                         PackageManagerTools packageManagerTools) {
            this.dataService = dataService;
            this.tokenStorage = tokenStorage;
            this.screenSwitcher = screenSwitcher;
            this.keyboardPresenter = keyboardPresenter;
            this.mPackageManagerTools = packageManagerTools;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            mPackages = mPackageManagerTools.getInstalledPackages();
            subscriptions = new CompositeSubscription();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMainScreen();
                }
            }, 5000);
        }

//        public void loadCategoryFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
//            final MainView view = getView();
//            if (view == null || subscriptions == null) {
//                return;
//            }
//            subscriptions.add(dataService.getGeneralFeed(from, to)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(observer));
//        }


        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

        public void openMainScreen() {
            screenSwitcher.open(new MainActivity.Screen());
        }

        public ArrayList<PInfo> getPackages() {
            return mPackageManagerTools.getInstalledPackages();
        }


        public ArrayList<PInfo> getmPackages() {
            return mPackages;
        }

    }
}
