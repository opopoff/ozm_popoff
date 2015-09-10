package com.umad.rly.ui.screen.main;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ozm.R;
import com.umad.rly.OzomeComponent;
import com.umad.rly.base.HasComponent;
import com.umad.rly.base.mvp.BasePresenter;
import com.umad.rly.base.mvp.BaseView;
import com.umad.rly.base.navigation.activity.ActivityScreen;
import com.umad.rly.data.DataService;
import com.umad.rly.data.RequestResultCodes;
import com.umad.rly.data.TokenStorage;
import com.umad.rly.data.analytics.LocalyticsController;
import com.umad.rly.data.api.response.ImageResponse;
import com.umad.rly.data.rx.EndlessObserver;
import com.umad.rly.data.social.SocialActivity;
import com.umad.rly.ui.ApplicationSwitcher;
import com.umad.rly.ui.screen.main.emotions.EmotionsPresenter;
import com.umad.rly.ui.screen.main.personal.OnBoardingDialogBuilder;
import com.umad.rly.ui.screen.main.personal.PersonalPresenter;
import com.umad.rly.data.SharingService;

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
        isActive = true;
        onBoardingDialogBuilder.attach(this);
        applicationSwitcher.attach(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
        applicationSwitcher.detach(this);
        onBoardingDialogBuilder.detach(this);
    }


    @Override
    protected void onDestroy() {
        component = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!presenter.onBackPressed()) {
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
        if (resultCode == RequestResultCodes.RESULT_CODE_UPDATE_FEED) {
            presenter.updateEmotionsFeed();
        }
    }

    @MainScope
    public static final class Presenter extends BasePresenter<MainView> {
        private static final int TALK_FRIEND_SHOW_FIRST = 3;
        private static final int TALK_FRIEND_SHOW_SECOND = 15;

        private final DataService dataService;
        private final SharingService sharingService;
        private final PersonalPresenter personalPresenter;
        private final EmotionsPresenter emotionsPresenter;
        private final TokenStorage tokenStorage;

        @Nullable
        private CompositeSubscription subscriptions;

        private boolean isNeedSwitch;

        @Inject
        public Presenter(DataService dataService, SharingService sharingService,
                         PersonalPresenter personalPresenter, EmotionsPresenter emotionsPresenter,
                         TokenStorage tokenStorage) {
            this.dataService = dataService;
            this.sharingService = sharingService;
            this.personalPresenter = personalPresenter;
            this.emotionsPresenter = emotionsPresenter;
            this.tokenStorage = tokenStorage;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();
            if (isNeedSwitch) {
                isNeedSwitch = false;
                openFirstTab();
            }
            //show SendFriendDialog
            final int startAppCounter = tokenStorage.getStartAppCounter();
            if (startAppCounter == TALK_FRIEND_SHOW_FIRST
                    || startAppCounter == TALK_FRIEND_SHOW_SECOND) {
                if (tokenStorage.getSendFriendDialogPreference() != startAppCounter) {
                    tokenStorage.setSendFriendDialogPreference(startAppCounter);
                    sharingService.showSendFriendsDialog();
                }
            }
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
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

        public void openFirstTab() {
            final MainView view = getView();
            if (view == null) return;
            view.openFirstScreen();
        }

        public void updateMyFeed() {
            personalPresenter.reloadFeed();
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
