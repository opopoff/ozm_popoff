package com.ozm.rocks.ui.screen.gold.novel;

import android.support.annotation.Nullable;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.screen.gold.GoldScope;
import com.ozm.rocks.ui.screen.sharing.SharingService;
import com.ozm.rocks.util.NetworkState;

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
public class GoldNovelPresenter extends BasePresenter<GoldNovelView> {
    private static final String KEY_LISTENER = "GoldNovelPresenter";

    private final DataService dataService;
    private final LocalyticsController localyticsController;
    private final SharingService sharingService;
    private final Category category;
    private final NetworkState networkState;

    @Nullable
    private CompositeSubscription subscriptions;

    private List<ImageResponse> mImageResponses = new ArrayList<>();

    @Inject
    public GoldNovelPresenter(DataService dataService,
                              LocalyticsController localyticsController,
                              SharingService sharingService,
                              @Named("category") Category category,
                              NetworkState networkState) {
        this.dataService = dataService;
        this.localyticsController = localyticsController;
        this.sharingService = sharingService;
        this.category = category;
        this.networkState = networkState;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
        if (mImageResponses.isEmpty()) {
            loadFeed(0);
        }
        if (!networkState.hasConnection()) {
            networkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
                @Override
                public void connectedState(boolean isConnected) {
                    if (isConnected) {
                        networkState.deleteConnectedListener(KEY_LISTENER);
                        loadFeed(0);
                    }
                }
            });
        }
    }

    public void loadFeed(int page) {
        final GoldNovelView view = getView();
        if (view == null || subscriptions == null) {
            return;
        }
        subscriptions.add(dataService.getCategoryFeed(category.id, page)
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
                                        Timber.w(throwable, "Gold. Load novel feed error!");
                                    }
                                })
        );
    }

    public void like(ImageResponse image) {
        sharingService.sendActionLikeDislike(SharingService.GOLD_RANDOM, image);
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
