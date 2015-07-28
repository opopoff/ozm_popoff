package com.ozm.rocks.ui.screen.main.emotions;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.RequestResultCodes;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.prefs.rating.RatingStorage;
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
    private final RatingStorage ratingStorage;
    private final TokenStorage tokenStorage;

    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;
    private List<ImageResponse> mSpecialProjectImages;

    @Inject
    public EmotionsPresenter(DataService dataService, ActivityScreenSwitcher screenSwitcher,
                             RatingStorage ratingStorage, TokenStorage tokenStorage) {
        this.dataService = dataService;
        this.screenSwitcher = screenSwitcher;
        this.ratingStorage = ratingStorage;
        this.tokenStorage = tokenStorage;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
        loadCategories();
    }

    public void loadCategories() {
        if (!checkView()) {
            return;
        }
        final EmotionsView view = getView();
        if (mCategory != null) {
            view.bindData(mCategory, showRating());
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
                                view.bindData(mCategory, showRating());
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

    private boolean showRating() {
        if (ratingStorage.getShowRatingDebug()){
            return true;
        }
        if (ratingStorage.getTimer() == 0) {
            ratingStorage.setTimer(System.currentTimeMillis());
        } else if (ratingStorage.getStatus() == RatingStorage.NOT_SHOWED
                && System.currentTimeMillis() - ratingStorage.getTimer()
                > RatingStorage.TWO_DAYS_IN_MILLISECONDS
                && tokenStorage.getStartAppCounter() > 4) {
            return true;
        } else if (ratingStorage.getStatus() == RatingStorage.IGNORED
                && System.currentTimeMillis() - ratingStorage.getTimer()
                > RatingStorage.WEEK_IN_MILLISECONDS) {
            return true;
        } else if (ratingStorage.getStatus() == RatingStorage.NOT_RATED
                && System.currentTimeMillis() - ratingStorage.getTimer()
                > RatingStorage.TWO_MONTHS_IN_MILLISECONDS) {
            return true;
        }
        return false;
    }

    public void setRatingStatus(@RatingStorage.RatingStatus int ratingStatus) {
        ratingStorage.setStatus(ratingStatus);
    }

    public void loadSpecialProject() {
        if (!checkView()) {
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
        if (checkView()) {
            getView().clearAdapter();
        }
        mCategory = null;
        loadCategories();
    }

    public void openGoldScreen(Category category) {
        screenSwitcher.openForResult(
                new GoldActivity.Screen(category, mCategory.categories.indexOf(category) == 0),
                RequestResultCodes.LIKE_HIDE_RESULT);
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
