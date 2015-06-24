package com.ozm.rocks.ui.gold.favorite;

import android.support.annotation.Nullable;

import com.ozm.R;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.gold.GoldScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@GoldScope
public class GoldFavoritePresenter extends BasePresenter<GoldFavoriteView> {

    private final DataService dataService;
    private final LocalyticsController localyticsController;
    private final Category category;

    @Nullable
    private CompositeSubscription subscriptions;

    private List<ImageResponse> mImageResponses = new ArrayList<>();

    @Inject
    public GoldFavoritePresenter(DataService dataService,
                                 LocalyticsController localyticsController,
                                 @Named("category") Category category) {
        this.dataService = dataService;
        this.localyticsController = localyticsController;
        this.category = category;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
        if (mImageResponses.isEmpty()) {
            int part = getView().getContext().getResources().getInteger(R.integer.page_part_count);
            loadFeed(0, part);
        }
    }

    public void loadFeed(int from, int to) {
        final GoldFavoriteView view = getView();
        if (view == null || subscriptions == null) {
            return;
        }
        if (category.isPromo) {
            localyticsController.pickupGoldenCollection();
        } else {
            localyticsController.pinGoldenCollection();
        }
        subscriptions.add(dataService.getGoldFeed(category.id, from, to)
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriptions != null) {
            subscriptions.unsubscribe();
            subscriptions = null;
        }
    }

}
