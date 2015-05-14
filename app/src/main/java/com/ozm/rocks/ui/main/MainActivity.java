package com.ozm.rocks.ui.main;

import android.os.Bundle;
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
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements HasComponent<MainComponent> {
    @Inject
    Presenter presenter;

    private MainComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_U2020);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerMainComponent.builder().
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
        return R.layout.main_layout;
    }

    @Override
    protected BasePresenter<? extends BaseView> presenter() {
        return presenter;
    }

    @Override
    protected int viewId() {
        return R.id.main_view;
    }

    @Override
    public MainComponent getComponent() {
        return component;
    }

    @MainScope
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
        }

        public void loadGeneralFeed(EndlessObserver<List<ImageResponse>> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getGeneralFeed()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

        public boolean isLoggedIn() {
            return tokenStorage.isAuthorized();
        }

        public void signIn(String email, String password) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.signIn(email, password).
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribeOn(Schedulers.io()).
                            subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean signed) {
                                    if (signed) {
                                        Timber.d("Signed in successfully");
                                        final MainView view = getView();
                                        if (view != null) {
                                            keyboardPresenter.hide();
                                            view.openMenu();
                                        }
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.e(throwable, "Error signing in");
                                }
                            })
            );
        }

        public void signOut() {
            final MainView view = getView();
            if (view == null) {
                return;
            }
            tokenStorage.clear();
            view.openLogin();
        }

        public void forgotPassword() {
            // TODO
        }

        public void openScreen(MainScreens screen) {
//            if (screen == MainMenuScreen.ACTIVATION) {
//                screenSwitcher.open(new QrActivationActivity.Screen());
//            }
            // TODO
        }

        public ArrayList<PInfo> getPackages() {
            return mPackageManagerTools.getInstalledPackages();
        }


        public ArrayList<PInfo> getmPackages() {
            return mPackages;
        }
    }
}
