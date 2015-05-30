package com.ozm.rocks.ui.emotions;

import android.app.Application;
import android.support.annotation.Nullable;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.categories.OneEmotionActivity;
import com.ozm.rocks.ui.gold.GoldActivity;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.NetworkState;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@MainScope
public final class MainEmotionsPresenter extends BasePresenter<MainEmotionsView> {
    private final DataService dataService;
    private final ActivityScreenSwitcher screenSwitcher;
    private final SharingService sharingService;
    private final KeyboardPresenter keyboardPresenter;
    private final NetworkState networkState;
    private final Application application;

    @Nullable
    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;

    @Inject
    public MainEmotionsPresenter(DataService dataService,
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
        subscriptions = new CompositeSubscription();
        loadCategories();
//        networkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
//            @Override
//            public void connectedState(boolean isConnected) {
//                showInternetMessage(!isConnected);
//            }
//        });
    }

    public void loadCategories() {
        final MainEmotionsView view = getView();
        if (view == null || subscriptions == null) {
            return;
        }
        subscriptions.add(dataService.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EndlessObserver<CategoryResponse>() {
                    @Override
                    public void onNext(CategoryResponse response) {
                        mCategory = response;
                        view.getEmotionsAdapter().addAll(mCategory.categories, mCategory.promos);
                    }
                }));
    }

    public void openGoldScreen(long categoryId, String categoryName) {
        screenSwitcher.openForResult(new GoldActivity.Screen(categoryId, categoryName), LikeHideResult
                .REQUEST_CODE);
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
}
