package com.ozm.rocks.ui.gold;

import android.app.Activity;
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
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.sharing.ChooseDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingService;

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

    @Inject
    ChooseDialogBuilder chooseDialogBuilder;

    private long categoryId;
    private String categoryName;
    private GoldComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        private final DataService dataService;
        private final ActivityScreenSwitcher screenSwitcher;
        private final long mCategoryId;
        private final String mCategoryName;
        private final LikeHideResult mLikeHideResult;
        private final SharingService sharingService;
        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService, ActivityScreenSwitcher screenSwitcher,
                         SharingService sharingService, @Named("category") long categoryId,
                         @Named("categoryName") String categoryName, LikeHideResult likeHideResult) {
            this.dataService = dataService;
            this.screenSwitcher = screenSwitcher;
            this.sharingService = sharingService;
            this.mCategoryId = categoryId;
            this.mCategoryName = categoryName;
            this.mLikeHideResult = likeHideResult;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();
            getView().toolbar.setTitle(mCategoryName);
            loadFeed(0, GoldView.DIFF_GRID_POSITION);
        }

        public void loadFeed(int from, int to) {
            final GoldView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getGoldFeed(mCategoryId, from, to)
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
            sharingService.showSharingDialog(imageResponse, SharingService.GOLD_CATEGORY_FEED);
        }

        public void setSharingDialogHide(SharingService.SharingDialogHide sharingDialogHide) {
            sharingService.setHideCallback(sharingDialogHide);
        }

        public void hide(HideRequest hideRequest) {
            final GoldView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.hide(hideRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
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
