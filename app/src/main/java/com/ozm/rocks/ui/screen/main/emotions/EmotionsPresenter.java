package com.ozm.rocks.ui.screen.main.emotions;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.screen.categories.LikeHideResult;
import com.ozm.rocks.ui.screen.gold.GoldActivity;
import com.ozm.rocks.ui.screen.main.MainScope;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@MainScope
public final class EmotionsPresenter extends BasePresenter<EmotionsView> {
    private final DataService dataService;
    private final ActivityScreenSwitcher screenSwitcher;

    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;
    private List<ImageResponse> mSpecialProjectImages;

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
        if (!checkView()){
            return;
        }
        final EmotionsView view = getView();
        if (mCategory != null) {
            view.bindData(mCategory);
            return;
        }

        subscriptions.add(dataService.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CategoryResponse>() {
                    @Override
                    public void call(CategoryResponse categoryResponse) {
                        mCategory = categoryResponse;
                        loadSpecialProject();
                        view.bindData(mCategory);
                    }
                }));
    }

    private void loadSpecialProject() {
        if (!checkView()){
            return;
        }
        final EmotionsView view = getView();
        if (mSpecialProjectImages != null) {
            view.bindSpecialProject(mSpecialProjectImages);
            return;
        }
        Category category = getSpecialProjectCategory();
        if (category == null) return;
        subscriptions.add(dataService.getGoldFeed(category.id, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<ImageResponse>>() {
                            @Override
                            public void call(List<ImageResponse> imageResponses) {
                                mSpecialProjectImages = imageResponses;
                                view.bindSpecialProject(mSpecialProjectImages);
                            }
                        }
                ));

    }

    private Category getSpecialProjectCategory() {
        final List<Category> categories = mCategory.categories;
        for (Category category : categories) {
            if (category.isPromo) {
                return category;
            }
        }
        return null;
    }

    public void reloadCategories() {
        mCategory = null;
        loadCategories();
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
