package com.ozm.rocks.ui.personal;

import android.app.Application;
import android.support.annotation.Nullable;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.categories.OneEmotionActivity;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.sharing.SharingActivity;
import com.ozm.rocks.ui.sharing.SharingService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@MainScope
public final class PersonalPresenter extends BasePresenter<PersonalView> {

    private final DataService dataService;
    private final ActivityScreenSwitcher screenSwitcher;
    private final SharingService sharingService;
    private final KeyboardPresenter keyboardPresenter;
    private final Application application;
    private final LikeHideResult mLikeHideResult;
    private Config mConfig;

    @Nullable
    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;

    @Inject
    public PersonalPresenter(DataService dataService,
                             ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                             Application application, SharingService sharingService,
                             LikeHideResult likeHideResult) {
        this.dataService = dataService;
        this.screenSwitcher = screenSwitcher;
        this.keyboardPresenter = keyboardPresenter;
        this.application = application;
        this.sharingService = sharingService;
        this.mLikeHideResult = likeHideResult;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
    }

    public void openShareScreen(ImageResponse imageResponse) {
        screenSwitcher.open(new SharingActivity.Screen(imageResponse, SharingService.PERSONAL));
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
        if (getView() != null) {
            getView().loadFeed();
        }
    }
}
