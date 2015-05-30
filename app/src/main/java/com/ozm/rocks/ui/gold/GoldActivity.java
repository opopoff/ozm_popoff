package com.ozm.rocks.ui.gold;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class GoldActivity extends BaseActivity implements HasComponent<GoldComponent> {
    @Inject
    Presenter presenter;

    @Inject
    SharingDialogBuilder sharingDialogBuilder;

    private long categoryId;
    private String categoryName;
    private GoldComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_U2020);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onExtractParams(@NonNull Bundle params) {
        super.onExtractParams(params);
        categoryId = params.getLong(Screen.BF_CATEGORY);
        categoryName = params.getString(Screen.BF_CATEGORY_NAME);
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerGoldComponent.builder().
                ozomeComponent(ozomeComponent).
                goldModule(new GoldModule(categoryId, categoryName)).build();
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
        return R.layout.gold_view;
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

        private static final String KEY_LISTENER = "Gold.Presenter";

        private final DataService dataService;
        private final TokenStorage tokenStorage;
        private final ActivityScreenSwitcher screenSwitcher;
        private final KeyboardPresenter keyboardPresenter;
        private final PackageManagerTools mPackageManagerTools;
        private final NetworkState networkState;
        private final long mCategoryId;
        private final String mCategoryName;
        private final LikeHideResult mLikeHideResult;
        private ArrayList<PInfo> mPackages;
        private final SharingService sharingService;
        @Nullable
        private CompositeSubscription subscriptions;
        private Config mConfig;
        private final Application application;

        @Inject
        public Presenter(DataService dataService, TokenStorage tokenStorage,
                         ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                         PackageManagerTools packageManagerTools, SharingService sharingService,
                         NetworkState networkState, Application application, @Named("category") long categoryId,
                         @Named("categoryName") String categoryName, LikeHideResult likeHideResult) {
            this.dataService = dataService;
            this.tokenStorage = tokenStorage;
            this.screenSwitcher = screenSwitcher;
            this.keyboardPresenter = keyboardPresenter;
            this.mPackageManagerTools = packageManagerTools;
            this.networkState = networkState;
            this.application = application;
            this.sharingService = sharingService;
            this.mCategoryId = categoryId;
            this.mCategoryName = categoryName;
            this.mLikeHideResult = likeHideResult;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            mPackages = sharingService.getPackages();
            subscriptions = new CompositeSubscription();
            getView().toolbar.setTitle(mCategoryName);
            networkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
                @Override
                public void connectedState(boolean isConnected) {
                    showInternetMessage(!isConnected);
                }
            });
            loadFeed(0, GoldView.DIFF_GRID_POSITION);
        }

        public void loadFeed(int from, int to) {
            final GoldView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getCategoryFeed(mCategoryId, from, to)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<List<ImageResponse>>() {
                                        @Override
                                        public void call(List<ImageResponse> imageResponses) {
                                            if (getView() != null) {
                                                getView().updateFeed(imageResponses);
                                            }
                                        }
                                    },
                                    new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Timber.w(throwable, "Gold. Load feed");
                                        }
                                    }
                            )
            );
        }

        public void shareWithDialog(ImageResponse imageResponse) {
            sharingService.showSharingDialog(imageResponse, SharingService.CATEGORY_FEED);
        }

        public void setSharingDialogHide(SharingService.SharingDialogHide sharingDialogHide) {
            sharingService.setHideCallback(sharingDialogHide);
        }

        public void showInternetMessage(boolean b) {
            final GoldView view = getView();
            if (view == null) {
                return;
            }
            view.noInternetView.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void goBack() {
            screenSwitcher.goBackResult(mLikeHideResult.isEmpty() ? LikeHideResult.EMPTY : LikeHideResult.FULL, null);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

    }

    public static final class Screen extends ActivityScreen {
        public static final String BF_CATEGORY = "GoldActivity.categoryId";
        public static final String BF_CATEGORY_NAME = "GoldActivity.categoryName";

        private final long categoryId;
        private final String categoryName;

        public Screen(long categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(BF_CATEGORY, categoryId);
            intent.putExtra(BF_CATEGORY_NAME, categoryName);
        }

        @Override
        protected Class<? extends Activity> activityClass() {
            return GoldActivity.class;
        }
    }
}
