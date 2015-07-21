package com.ozm.rocks.ui.screen.main;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
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
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.data.social.SocialActivity;
import com.ozm.rocks.ui.ApplicationSwitcher;
import com.ozm.rocks.ui.screen.categories.LikeHideResult;
import com.ozm.rocks.ui.screen.categories.OneEmotionActivity;
import com.ozm.rocks.ui.screen.gold.GoldActivity;
import com.ozm.rocks.ui.screen.main.emotions.EmotionsPresenter;
import com.ozm.rocks.ui.screen.main.personal.OnBoardingDialogBuilder;
import com.ozm.rocks.ui.screen.main.personal.PersonalPresenter;
import com.ozm.rocks.ui.screen.sharing.ChooseDialogBuilder;
import com.ozm.rocks.ui.screen.sharing.SharingDialogBuilder;
import com.ozm.rocks.ui.screen.sharing.SharingService;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends SocialActivity implements HasComponent<MainComponent> {

    public static final String WP_OPEN_FROM_WIDGET = "MainActivity.widget";

    @Inject
    Presenter presenter;

    @Inject
    SharingDialogBuilder sharingDialogBuilder;

    @Inject
    ChooseDialogBuilder chooseDialogBuilder;

    @Inject
    OnBoardingDialogBuilder onBoardingDialogBuilder;

    @Inject
    LocalyticsController localyticsController;

    @Inject
    ApplicationSwitcher applicationSwitcher;

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
        onBoardingDialogBuilder.attach(this);
        applicationSwitcher.attach(this);
    }

    @Override
    protected void onStop() {
        isActive = false;
        applicationSwitcher.detach();
        sharingDialogBuilder.detach();
        onBoardingDialogBuilder.detach();
        chooseDialogBuilder.detach();
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        component = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!presenter.onBackPressed()){
            super.onBackPressed();
        }
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
        if (resultCode == GoldActivity.RESULT_CODE_UPDATE_FEED) {
            presenter.updateEmotionsFeed();
        }
    }

    @MainScope
    public static final class Presenter extends BasePresenter<MainView> {

        private final DataService dataService;
        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingService sharingService;
        private final PersonalPresenter personalPresenter;
        private final EmotionsPresenter emotionsPresenter;
        private final TokenStorage tokenStorage;
        private final SendFriendDialogBuilder sendFriendDialogBuilder;

        @Nullable
        private CompositeSubscription subscriptions;

        private boolean isNeedSwitch;

        @Inject
        public Presenter(DataService dataService, ActivityScreenSwitcher screenSwitcher,
                         SharingService sharingService,
                         PersonalPresenter personalPresenter, EmotionsPresenter emotionsPresenter,
                         TokenStorage tokenStorage, SendFriendDialogBuilder sendFriendDialogBuilder) {
            this.dataService = dataService;
            this.screenSwitcher = screenSwitcher;
            this.sharingService = sharingService;
            this.personalPresenter = personalPresenter;
            this.emotionsPresenter = emotionsPresenter;
            this.tokenStorage = tokenStorage;
            this.sendFriendDialogBuilder = sendFriendDialogBuilder;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            if (isNeedSwitch) {
                isNeedSwitch = false;
                openFirstTab();
            }
            if (tokenStorage.getStartAppCounter() == 3 ||
                tokenStorage.getStartAppCounter() == 15) {
                if (tokenStorage.getSendFriendDialogPreference() != tokenStorage.getStartAppCounter()){
                    tokenStorage.setSendFriendDialogPreference(tokenStorage.getStartAppCounter());
                    sharingService.showSendFriendsDialog();
                }

            }

            sharingService.reloadConfig(null, tokenStorage.getVkData());
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
            if (!checkView()) {
                return;
            }
            if (subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getMyCollection()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
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

        public void updateMyFeed() {
            personalPresenter.updateFeed();
        }

        public void setSwitchToFirstTab() {
            this.isNeedSwitch = true;
        }

        public void updateEmotionsFeed() {
            emotionsPresenter.reloadCategories();
        }

        public boolean onBackPressed() {
            if (!checkView()) {
                return false;
            }
            MainView view = getView();
            return view.onBackPressed();
        }
    }

    public static final class Screen extends ActivityScreen {
        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        @Override
        protected Bundle activityOptions(Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return ActivityOptions.makeCustomAnimation(activity.getApplicationContext(),
                        R.anim.fade_in, R.anim.fade_out).toBundle();
            } else {
                return super.activityOptions(activity);
            }
        }

        @Override
        protected Class<? extends Activity> activityClass() {
            return MainActivity.class;
        }
    }
}