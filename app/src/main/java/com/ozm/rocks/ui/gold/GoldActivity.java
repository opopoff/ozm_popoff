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
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreen;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.CategoryPinRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.vk.VkActivity;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.sharing.ChooseDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.Timestamp;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class GoldActivity extends VkActivity implements HasComponent<GoldComponent> {
    @Inject
    Presenter presenter;

    @Inject
    SharingDialogBuilder sharingDialogBuilder;

    @Inject
    ChooseDialogBuilder chooseDialogBuilder;

    public static final int UPDATE_REQUEST_CODE = 1444;
    private Category category;
    private boolean isFirst;
    private GoldComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
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
        private final Category mCategory;
        private final LikeHideResult mLikeHideResult;
        private final SharingService sharingService;
        private final LocalyticsController localyticsController;
        private final boolean isFirst;

        @Nullable
        private CompositeSubscription subscriptions;

        private List<ImageResponse> mImageResponses = new ArrayList<>();

        @Inject
        public Presenter(DataService dataService, ActivityScreenSwitcher screenSwitcher,
                         SharingService sharingService, @Named("category") Category category,
                         LikeHideResult likeHideResult, @Named("isFirst") boolean isFirst,
                         LocalyticsController localyticsController) {
            this.dataService = dataService;
            this.screenSwitcher = screenSwitcher;
            this.sharingService = sharingService;
            this.mCategory = category;
            this.mLikeHideResult = likeHideResult;
            this.isFirst = isFirst;
            this.localyticsController = localyticsController;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();
            getView().toolbar.setTitle(mCategory.description);
            if (mImageResponses.isEmpty()) {
                loadFeed(0, GoldView.DATA_PART);
            }
            getView().setToolbarMenu(mCategory, isFirst);
        }

        public void loadFeed(int from, int to) {
            final GoldView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            if (mCategory.isPromo) {
                localyticsController.pickupGoldenCollection();
            } else {
                localyticsController.pinGoldenCollection();
            }
            subscriptions.add(dataService.getGoldFeed(mCategory.id, from, to)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<List<ImageResponse>>() {
                                        @Override
                                        public void call(List<ImageResponse> imageResponses) {
                                            mImageResponses.addAll(imageResponses);
                                            if (view != null) {
                                                view.updateFeed(imageResponses);
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

        public void pin() {
            final GoldView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(Action.getPinUnpinAction(Timestamp.getUTC(), mCategory.id));
            subscriptions.add(dataService.pin(new CategoryPinRequest(actions))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<String>() {
                                        @Override
                                        public void call(String s) {
                                            Timber.d(s, "pin: success");
                                            screenSwitcher.setResult(UPDATE_REQUEST_CODE, null);
                                            Timber.d("BackResult: 1");
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

        public void shareWithDialog(ImageResponse imageResponse) {
            sharingService.showSharingDialog(imageResponse, SharingService.GOLD_CATEGORY_FEED);
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

    }

    public static final class Screen extends ActivityScreen {
        public static final String BF_CATEGORY = "SharingActivity.category";
        public static final String BF_IS_FIRST = "SharingActivity.isFirst";

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
