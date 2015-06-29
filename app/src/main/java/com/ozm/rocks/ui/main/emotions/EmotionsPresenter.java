package com.ozm.rocks.ui.main.emotions;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.gold.GoldActivity;
import com.ozm.rocks.ui.main.MainScope;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@MainScope
public final class EmotionsPresenter extends BasePresenter<EmotionsView> {
    private final DataService dataService;
    private final ActivityScreenSwitcher screenSwitcher;

    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;

    @Inject
    public EmotionsPresenter(DataService dataService, ActivityScreenSwitcher screenSwitcher) {
        this.dataService = dataService;
        this.screenSwitcher = screenSwitcher;
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
        final EmotionsView view = getView();
        if (view == null) {
            return;
        }
//        if (mCategory != null) {
//            view.bindData(mCategory);
//            return;
//        }

        subscriptions.add(dataService.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EndlessObserver<CategoryResponse>() {
                    @Override
                    public void onNext(CategoryResponse response) {
                        mCategory = response;
                        view.bindData(mCategory);
                    }
                }));
    }

    public void openGoldScreen(Category category) {
        screenSwitcher.openForResult(
                new GoldActivity.Screen(category, mCategory.categories.indexOf(category) == 0),
                LikeHideResult.REQUEST_CODE);
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
