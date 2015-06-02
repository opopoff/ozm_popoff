package com.ozm.rocks.ui.my;

import android.app.Application;
import android.support.annotation.Nullable;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.NetworkState;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@MainScope
public final class MainMyCollectionPresenter extends BasePresenter<MainMyCollectionView> {

    private final DataService dataService;
    private final ActivityScreenSwitcher screenSwitcher;
    private final SharingService sharingService;
    private final KeyboardPresenter keyboardPresenter;
    private final NetworkState networkState;
    private final Application application;
    private final LikeHideResult mLikeHideResult;
    private Config mConfig;

    @Nullable
    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;

    @Inject
    public MainMyCollectionPresenter(DataService dataService,
                                     ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                                     NetworkState networkState, Application application, SharingService sharingService,
                                     LikeHideResult likeHideResult) {
        this.dataService = dataService;
        this.screenSwitcher = screenSwitcher;
        this.keyboardPresenter = keyboardPresenter;
        this.application = application;
        this.networkState = networkState;
        this.sharingService = sharingService;
        this.mLikeHideResult = likeHideResult;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
        updateFeed();
    }

    public void shareWithDialog(ImageResponse imageResponse) {
        sharingService.showSharingDialog(imageResponse, SharingService.PERSONAL);
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

    public void updateFeed() {
        if (getView() != null){
            getView().loadFeed();
        }
    }
}
