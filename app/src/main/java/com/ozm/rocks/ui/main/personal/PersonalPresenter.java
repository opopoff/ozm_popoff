package com.ozm.rocks.ui.main.personal;

import android.app.Application;
import android.support.annotation.Nullable;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.navigation.activity.ActivityScreen;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.sharing.SharingActivity;
import com.ozm.rocks.ui.sharing.SharingService;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@MainScope
public final class PersonalPresenter extends BasePresenter<PersonalView> {

    private final DataService dataService;
    private final ActivityScreenSwitcher screenSwitcher;
    private final SharingService sharingService;
    private final TokenStorage tokenStorage;
    private final OnBoardingDialogBuilder onBoardingDialogBuilder;
    private final KeyboardPresenter keyboardPresenter;
    private final Application application;
    private final LikeHideResult mLikeHideResult;
    private final LocalyticsController localyticsController;
    private Config mConfig;

    @Nullable
    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;

    @Inject
    public PersonalPresenter(DataService dataService,
                             ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                             Application application, SharingService sharingService, TokenStorage tokenStorage,
                             OnBoardingDialogBuilder onBoardingDialogBuilder, LikeHideResult likeHideResult,
                             LocalyticsController localyticsController) {
        this.dataService = dataService;
        this.screenSwitcher = screenSwitcher;
        this.keyboardPresenter = keyboardPresenter;
        this.application = application;
        this.sharingService = sharingService;
        this.onBoardingDialogBuilder = onBoardingDialogBuilder;
        this.mLikeHideResult = likeHideResult;
        this.tokenStorage = tokenStorage;
        this.localyticsController = localyticsController;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
        onBoardingDialogBuilder.setCallBack(new OnBoardingDialogBuilder.ChooseDialogCallBack() {
            @Override
            public void apply() {
                tokenStorage.setCreateAlbum(true);
                localyticsController.showAlbumOnBoarding(LocalyticsController.CREATE);
            }

            @Override
            public void cancel() {
                localyticsController.showAlbumOnBoarding(LocalyticsController.SKIP);
            }
        });
    }

    public void openOnBoardingDialog() {
        if (!tokenStorage.isPersonalPopupShowed() && !tokenStorage.isCreateAlbum()) {
            tokenStorage.setPersonalPopupShowed();
            onBoardingDialogBuilder.openDialog();
        }
    }

    public void openShareScreen(ImageResponse imageResponse) {
        ActivityScreen screen = new SharingActivity.Screen(imageResponse, SharingService.PERSONAL);
        screenSwitcher.open(screen);
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
