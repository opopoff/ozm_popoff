package com.ozm.fun.ui.screen.gold;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ozm.R;
import com.ozm.fun.OzomeComponent;
import com.ozm.fun.base.HasComponent;
import com.ozm.fun.base.mvp.BasePresenter;
import com.ozm.fun.base.mvp.BaseView;
import com.ozm.fun.base.navigation.activity.ActivityScreen;
import com.ozm.fun.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.fun.data.DataService;
import com.ozm.fun.data.RequestResultCodes;
import com.ozm.fun.data.TokenStorage;
import com.ozm.fun.data.analytics.LocalyticsController;
import com.ozm.fun.data.api.request.Action;
import com.ozm.fun.data.api.request.CategoryPinRequest;
import com.ozm.fun.data.api.response.Category;
import com.ozm.fun.data.api.response.ImageResponse;
import com.ozm.fun.data.social.SocialActivity;
import com.ozm.fun.ui.screen.gold.favorite.GoldFavoritePresenter;
import com.ozm.fun.ui.screen.gold.novel.GoldNovelPresenter;
import com.ozm.fun.ui.screen.sharing.SharingActivity;
import com.ozm.fun.data.SharingService;
import com.ozm.fun.util.Timestamp;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RequestResultCodes.RESULT_CODE_HIDE_IMAGE) {
            ImageResponse imageResponse = data.getParcelableExtra(RequestResultCodes.IMAGE_RESPONSE_KEY);
            presenter.hideImage(imageResponse);
        } else if (resultCode == RequestResultCodes.RESULT_CODE_LIKE_IMAGE ||
                resultCode == RequestResultCodes.RESULT_CODE_SHARE_IMAGE) {
            ImageResponse imageResponse = data.getParcelableExtra(RequestResultCodes.IMAGE_RESPONSE_KEY);
            presenter.setLikeShareImage(imageResponse, resultCode);
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

        private static final String SP_IS_ONBOARDING_SHOW = "gold.activity.SP_IS_ONBOARDING_SHOW";
        private static final String SP_IS_FIRST_KEY = "gold.activity.SP_IS_FIRST_KEY";
        private final DataService dataService;
        private final ActivityScreenSwitcher screenSwitcher;
        private final LocalyticsController localyticsController;
        private final TokenStorage tokenStorage;
        private final GoldFavoritePresenter goldFavoritePresenter;
        private final GoldNovelPresenter goldNovelPresenter;
        private final Category mCategory;
        private boolean isFirst;
        private boolean isOnboardongShow;

        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService,
                         ActivityScreenSwitcher screenSwitcher,
                         LocalyticsController localyticsController,
                         @Named(GoldModule.CATEGORY) Category category,
                         @Named(GoldModule.ISFIRST) boolean isFirst,
                         TokenStorage tokenStorage,
                         GoldFavoritePresenter goldFavoritePresenter,
                         GoldNovelPresenter goldNovelPresenter) {
            this.dataService = dataService;
            this.localyticsController = localyticsController;
            this.screenSwitcher = screenSwitcher;
            this.tokenStorage = tokenStorage;
            this.mCategory = category;
            this.isFirst = isFirst;
            this.goldFavoritePresenter = goldFavoritePresenter;
            this.goldNovelPresenter = goldNovelPresenter;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();

            final GoldView view = getView();
            if (!isFirst) {
                Timber.d("OnBoarding: if (!isFirst)" );
                Timber.d("OnBoarding: %s, %s", isFirst ? "true" : "false", isOnboardongShow ? "true" : "false");
//                if (!mCategory.isPromo && !isOnboardongShow) {
//                    Timber.d("OnBoarding: up");
//                    tokenStorage.upGoldFirstOnBoarding();
//                    isOnboardongShow = true;
//                    if (tokenStorage.getGoldFourOnBoarding() == 3 && !tokenStorage.isUpFolder()) {
//                        Timber.d("OnBoarding: show");
//                        view.showFourOnBoarding();
//                    }
//                }
            }
            if (mCategory.isNew) {
                screenSwitcher.setResult(RequestResultCodes.RESULT_CODE_UPDATE_FEED, null);
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
//            view.showPinMessage(mCategory);
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(Action.getPinUnpinAction(Timestamp.getUTC(), mCategory.id));
            subscriptions.add(
                    dataService.pin(new CategoryPinRequest(actions))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<String>() {
                                        @Override
                                        public void call(String s) {
                                            screenSwitcher.setResult(RequestResultCodes.RESULT_CODE_UPDATE_FEED, null);
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

        public void hideImage(ImageResponse imageResponse) {
            goldFavoritePresenter.hideResult(imageResponse);
            goldNovelPresenter.hideResult(imageResponse);
        }

        public void moveItem(ImageResponse image) {
            if (checkView()) {
                final GoldView view = getView();
                view.moveItem(image);
            }
        }


        public void setLikeShareImage(ImageResponse imageResponse, int resultCode) {
            goldFavoritePresenter.likeShareResult(new ImageResponse(imageResponse), resultCode);
            goldNovelPresenter.likeShareResult(new ImageResponse(imageResponse), resultCode);
        }

        public void openShareScreen(ImageResponse imageResponse, @SharingService.From int from) {
            screenSwitcher.openForResult(new SharingActivity.Screen(imageResponse, from),
                    RequestResultCodes.REQUEST_OPEN_SHARING_SCREEN);
        }

        public void goBack() {
            screenSwitcher.goBack();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

        @Override
        protected void onRestore(@NonNull Bundle savedInstanceState) {
            super.onRestore(savedInstanceState);
            isFirst = savedInstanceState.getBoolean(SP_IS_FIRST_KEY);
            isOnboardongShow = savedInstanceState.getBoolean(SP_IS_ONBOARDING_SHOW);
        }

        @Override
        protected void onSave(@NonNull Bundle outState) {
            outState.putBoolean(SP_IS_FIRST_KEY, isFirst);
            outState.putBoolean(SP_IS_ONBOARDING_SHOW, isOnboardongShow);
            super.onSave(outState);
        }

        public boolean onBackPressed() {
            if (!checkView()) {
                return false;
            }
            final GoldView view = getView();
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
