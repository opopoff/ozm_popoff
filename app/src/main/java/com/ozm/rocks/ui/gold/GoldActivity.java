package com.ozm.rocks.ui.gold;

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
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.CategoryPinRequest;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.social.SocialActivity;
import com.ozm.rocks.ui.sharing.ChooseDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingActivity;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.Timestamp;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class GoldActivity extends SocialActivity implements HasComponent<GoldComponent> {
    @Inject
    Presenter presenter;

    @Inject
    SharingDialogBuilder sharingDialogBuilder;

    @Inject
    ChooseDialogBuilder chooseDialogBuilder;

    public static final int RESULT_CODE_UPDATE_FEED = 1444;
    private Category category;
    private boolean isFirst;
    private GoldComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onExtractParams(@NonNull Bundle params) {
        super.onExtractParams(params);
        category = params.getParcelable(Screen.BF_CATEGORY);
        isFirst = params.getBoolean(Screen.BF_IS_FIRST);
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerGoldComponent.builder().
                ozomeComponent(ozomeComponent).
                goldModule(new GoldModule(category, isFirst)).build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharingDialogBuilder.attach(this);
        chooseDialogBuilder.attach(this);
    }

    @Override
    protected void onStop() {
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
    public void onBackPressed() {
        if (!presenter.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.gold_layout;
    }

    @Override
    protected BasePresenter<? extends BaseView> presenter() {
        return presenter;
    }

    @Override
    protected int viewId() {
        return R.id.gold_view;
    }

    @Override
    public GoldComponent getComponent() {
        return component;
    }

    @GoldScope
    public static final class Presenter extends BasePresenter<GoldView> {

        private final DataService dataService;
        private final ActivityScreenSwitcher screenSwitcher;
        private final LocalyticsController localyticsController;
        private final TokenStorage tokenStorage;

        private final Category mCategory;
        private final boolean isFirst;

        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService,
                         ActivityScreenSwitcher screenSwitcher,
                         LocalyticsController localyticsController,
                         @Named(GoldModule.CATEGORY) Category category,
                         @Named(GoldModule.ISFIRST) boolean isFirst,
                         TokenStorage tokenStorage) {
            this.dataService = dataService;
            this.localyticsController = localyticsController;
            this.screenSwitcher = screenSwitcher;
            this.tokenStorage = tokenStorage;
            this.mCategory = category;
            this.isFirst = isFirst;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();

            final GoldView view = getView();
            if (!isFirst) {
                if (tokenStorage.getGoldFourOnBoarding() == 3 && !tokenStorage.isUpFolder()) {
                    view.showFourOnBoarding();
                }
                if (!mCategory.isPromo) {
                    tokenStorage.upGoldFirstOnBoarding();
                }
            }
        }

        public void pin() {
            if (mCategory.isPromo) {
                localyticsController.pinGoldenCollection();
            } else {
                localyticsController.pickupGoldenCollection();
            }
            tokenStorage.setUpFolder();
            final GoldView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            getView().showPinMessage(mCategory);
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(Action.getPinUnpinAction(Timestamp.getUTC(), mCategory.id));
            subscriptions.add(dataService.pin(new CategoryPinRequest(actions))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<String>() {
                                        @Override
                                        public void call(String s) {
                                            screenSwitcher.setResult(GoldActivity.RESULT_CODE_UPDATE_FEED, null);
                                        }
                                    },
                                    new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Timber.d(throwable, "pin: error");
                                        }
                                    })
            );
        }

        public Category getCategory() {
            return mCategory;
        }

        public boolean isFirst() {
            return isFirst;
        }

        public void openShareScreen(ImageResponse imageResponse, @SharingService.From int from) {
            screenSwitcher.open(new SharingActivity.Screen(imageResponse, from));
        }

        public void goBack() {
            screenSwitcher.goBack();
        }

        public void moveItem(ImageResponse image) {
            final GoldView view = getView();
            if (view == null) return;
            view.moveItem(image);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

        public boolean onBackPressed() {
            final GoldView view = getView();
            if (view == null) {
                return false;
            }
            return view.onBackPressed();
        }
    }

    public static final class Screen extends ActivityScreen {
        public static final String BF_CATEGORY = "GoldActivity.category";
        public static final String BF_IS_FIRST = "GoldActivity.isFirst";

        private final Category category;
        private final boolean isFirst;

        public Screen(Category category, boolean isFirst) {
            this.category = category;
            this.isFirst = isFirst;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(BF_CATEGORY, category);
            intent.putExtra(BF_IS_FIRST, isFirst);
        }

        @Override
        protected Class<? extends Activity> activityClass() {
            return GoldActivity.class;
        }
    }
}
