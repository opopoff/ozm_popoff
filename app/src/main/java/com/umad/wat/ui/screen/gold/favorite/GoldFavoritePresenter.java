package com.umad.wat.ui.screen.gold.favorite;

import android.support.annotation.Nullable;

import com.umad.wat.base.mvp.BasePresenter;
import com.umad.wat.data.DataService;
import com.umad.wat.data.api.response.Category;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.ui.screen.gold.GoldModule;
import com.umad.wat.ui.screen.gold.GoldScope;
import com.umad.wat.data.SharingService;
import com.umad.wat.util.NetworkState;

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
    private static final String KEY_LISTENER = "GoldFavoritePresenter";

    private final DataService dataService;
    private final SharingService sharingService;
    private final Category category;
    private final NetworkState networkState;

    @Nullable
    private CompositeSubscription subscriptions;

    private List<ImageResponse> mImageResponses = new ArrayList<>();

    @Inject
    public GoldFavoritePresenter(DataService dataService,
                                 SharingService sharingService,
                                 @Named(GoldModule.CATEGORY) Category category,
                                 NetworkState networkState) {
        this.dataService = dataService;
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
        final GoldFavoriteView view = getView();
        if (view == null || subscriptions == null) {
            return;
        }
        subscriptions.add(dataService.getGoldFeed(category.id, page)
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
                                        Timber.w(throwable, "Gold. Load favorite feed error!");
                                    }
                                }
                        )
        );
    }

    public void like(ImageResponse image) {
        sharingService.sendActionLikeDislike(SharingService.GOLD_FAVORITES, image);
    }

    public void likeShareResult(ImageResponse imageResponse, int resultCode) {
        if (checkView()) {
            getView().likeShareImage(imageResponse, resultCode);
        }
    }

    public void hideResult(ImageResponse imageResponse) {
        if (checkView()) {
            getView().hideImage(imageResponse);
        }
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
