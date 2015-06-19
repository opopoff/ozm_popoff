package com.ozm.rocks.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreen;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.data.vk.VkActivity;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.categories.OneEmotionActivity;
import com.ozm.rocks.ui.emotions.EmotionsPresenter;
import com.ozm.rocks.ui.general.GeneralPresenter;
import com.ozm.rocks.ui.gold.GoldActivity;
import com.ozm.rocks.ui.personal.PersonalPresenter;
import com.ozm.rocks.ui.sharing.ChooseDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingService;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends VkActivity implements HasComponent<MainComponent> {

    public static final String WP_OPEN_FROM_WIDGET = "MainActivity.widget";

    @Inject
    Presenter presenter;

    @Inject
    SharingDialogBuilder sharingDialogBuilder;

    @Inject
    ChooseDialogBuilder chooseDialogBuilder;

    @Inject
    LocalyticsController localyticsController;

    private MainComponent component;

    private boolean isActive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(WP_OPEN_FROM_WIDGET)) {
            localyticsController.openApp(LocalyticsController.WIDGET);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.hasExtra(WP_OPEN_FROM_WIDGET)) {
            if (isActive) {
                presenter.openFirstTab();
            } else {
                presenter.setSwitchToFirstTab();
            }
            localyticsController.openApp(LocalyticsController.WIDGET);
        }
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerMainComponent.builder().
                mainModule(new MainModule(this)).
                ozomeComponent(ozomeComponent).build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
        sharingDialogBuilder.attach(this);
        chooseDialogBuilder.attach(this);
    }

    @Override
    protected void onStop() {
        isActive = false;
        sharingDialogBuilder.detach();
        chooseDialogBuilder.detach();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("BackResult: requestCode=%d, resultCode=%d", requestCode, resultCode);
        if (requestCode == LikeHideResult.REQUEST_CODE && resultCode == LikeHideResult.FULL) {
            presenter.handleLikeDislikeResult();
        }
        if (resultCode == GoldActivity.UPDATE_REQUEST_CODE){
            Timber.d("BackResult: 3");
            presenter.updateEmotionsFeed();
        }
     }

    @MainScope
    public static final class Presenter extends BasePresenter<MainView> {

        private final DataService dataService;
        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;
        private final GeneralPresenter generalPresenter;
        private final PersonalPresenter personalPresenter;
        private final EmotionsPresenter emotionsPresenter;
        @Nullable
        private CompositeSubscription subscriptions;

        private boolean isNeedSwitch;

        @Inject
        public Presenter(DataService dataService, ActivityScreenSwitcher screenSwitcher,
                         SharingService sharingService, GeneralPresenter generalPresenter,
                         PersonalPresenter personalPresenter, EmotionsPresenter emotionsPresenter) {
            this.dataService = dataService;
            this.screenSwitcher = screenSwitcher;
            this.sharingService = sharingService;
            this.generalPresenter = generalPresenter;
            this.personalPresenter = personalPresenter;
            this.emotionsPresenter = emotionsPresenter;

        }

        @Override
        protected void onLoad() {
            super.onLoad();
            if (isNeedSwitch) {
                isNeedSwitch = false;
                openFirstTab();
            }

            subscriptions = new CompositeSubscription();
        }

        public void loadGeneralFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
            if (!checkView()) return;
            final MainView view = getView();
            ;
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getGeneralFeed(from, to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void setSharingDialogHide(SharingService.SharingDialogHide sharingDialogHide) {
            sharingService.setHideCallback(sharingDialogHide);
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

        public void like(LikeRequest likeRequest) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.like(likeRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        public void dislike(DislikeRequest dislikeRequest) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.dislike(dislikeRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        public void saveImage(String url, String sharingUrl) {
            if (subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.createImage(url, sharingUrl)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {

                                        }
                                    },
                                    new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Timber.w(throwable, "Save image");
                                        }
                                    }
                            )
            );
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
                            .subscribe(
                                    new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {

                                        }
                                    },
                                    new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Timber.w(throwable, "Delete image");
                                        }
                                    }

                            )
            );
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            sharingService.unsubscribe();
            if (subscriptions != null) {
                sharingService.unsubscribe();
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

        public void openFirstTab() {
            final MainView view = getView();
            if (view == null) return;
            view.openFirstScreen();
        }

        public void openOneEmotionScreen(long categoryId, String categoryName) {
            screenSwitcher.openForResult(new OneEmotionActivity.Screen(categoryId, categoryName),
                    LikeHideResult.REQUEST_CODE);
        }

        public void handleLikeDislikeResult() {
            generalPresenter.checkResult();
        }

        public void updateMyFeed() {
            personalPresenter.updateFeed();
        }

        public void setSwitchToFirstTab() {
            this.isNeedSwitch = true;
        }

        public void pageChanged() {
            generalPresenter.hideOnBoarding();
        }

        public void updateEmotionsFeed() {
            emotionsPresenter.loadCategories();
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
