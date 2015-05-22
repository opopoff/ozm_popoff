package com.ozm.rocks.ui.main;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreen;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.emotionList.OneEmotionActivity;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.NetworkState;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity implements HasComponent<MainComponent> {
    @Inject
    Presenter presenter;

    @Inject
    SharingDialogBuilder sharingDialogBuilder;
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
    protected void onStart() {
        super.onStart();
        sharingDialogBuilder.attach(this);
    }

    @Override
    protected void onStop() {
        sharingDialogBuilder.detach();
        super.onStop();
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
        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;
        private final KeyboardPresenter keyboardPresenter;
        private final NetworkState networkState;
        private final Application application;

        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService,
                         ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                         NetworkState networkState, Application application, SharingService sharingService) {
            this.dataService = dataService;
            this.screenSwitcher = screenSwitcher;
            this.keyboardPresenter = keyboardPresenter;
            this.application = application;
            this.networkState = networkState;
            this.sharingService = sharingService;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            sharingService.sendPackages();
            subscriptions = new CompositeSubscription();
            networkState.addConnectedListener(new NetworkState.IConnected() {
                @Override
                public void connectedState(boolean isConnected) {
                    showInternetMessage(!isConnected);
                }
            });
        }

        public void loadGeneralFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getGeneralFeed(from, to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void shareWithDialog(ImageResponse imageResponse) {
            sharingService.showSharingDialog(imageResponse);
        }

        public void updateGeneralFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.generalFeedUpdate(from, to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void loadMyCollection(EndlessObserver<List<ImageResponse>> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getMyCollection()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void like(LikeRequest likeRequest, EndlessObserver<String> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.like(likeRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void dislike(DislikeRequest dislikeRequest, EndlessObserver<String> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.dislike(dislikeRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void saveImage(String url) {
            if (subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.createImage(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        public void hide(HideRequest hideRequest) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.hide(hideRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        public void deleteImage(final ImageResponse image) {
            if (subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.deleteImage(image.url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (subscriptions != null) {
                sharingService.unsubscribe();
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

        public void openScreen(MainScreens screen) {
//            if (screen == MainMenuScreen.ACTIVATION) {
//                screenSwitcher.open(new QrActivationActivity.Screen());
//            }
            // TODO
        }


        public void showInternetMessage(boolean b) {
            final MainView view = getView();
            if (view == null) {
                return;
            }
            view.mNoInternetView.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void openOneEmotionScreen(long categoryId, String categoryName) {
            screenSwitcher.open(new OneEmotionActivity.Screen(categoryId, categoryName));
        }
    }

    public static final class Screen extends ActivityScreen {
        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        @Override
        protected Class<? extends Activity> activityClass() {
            return MainActivity.class;
        }
    }
}
