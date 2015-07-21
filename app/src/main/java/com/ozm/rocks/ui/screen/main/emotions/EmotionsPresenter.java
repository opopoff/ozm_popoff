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
import com.ozm.rocks.util.Strings;
import com.ozm.rocks.util.Timestamp;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

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

        final long timestamp = Timestamp.getUTC();
        subscriptions.add(dataService.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<CategoryResponse>() {
                            @Override
                            public void call(CategoryResponse categoryResponse) {
                                Timber.d("EmotionsPresenter: DataService.loadCategories() time = %d seconds",
                                        Timestamp.getUTC() - timestamp);
                                mCategory = categoryResponse;
                                view.bindData(mCategory);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.e(throwable, "EmotionsPresenter: DataService.loadCategories() error");
                            }
                        }
                ));
    }

    public void loadSpecialProject() {
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
        if (Strings.isBlank(category.promoBackgroundImage)) {
            //for compatibility
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
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.e(throwable, "EmotionsPresenter: DataService.getGoldFeed() error");
                                }
                            }
                    ));
        } else {
            view.bindSpecialProject(category.promoBackgroundImage);
        }
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
