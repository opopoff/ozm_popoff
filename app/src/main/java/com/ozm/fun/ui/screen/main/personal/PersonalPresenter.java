package com.ozm.fun.ui.screen.main.personal;

import android.support.annotation.Nullable;

import com.ozm.fun.base.mvp.BasePresenter;
import com.ozm.fun.base.navigation.activity.ActivityScreen;
import com.ozm.fun.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.fun.data.DataService;
import com.ozm.fun.data.SharingService;
import com.ozm.fun.data.TokenStorage;
import com.ozm.fun.data.analytics.LocalyticsController;
import com.ozm.fun.data.api.response.ImageResponse;
import com.ozm.fun.ui.screen.main.MainScope;
import com.ozm.fun.ui.screen.sharing.SharingActivity;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@MainScope
public final class PersonalPresenter extends BasePresenter<PersonalView> {

    private final DataService dataService;
    private final ActivityScreenSwitcher screenSwitcher;
    private final SharingService sharingService;
    private final TokenStorage tokenStorage;
    private final OnBoardingDialogBuilder onBoardingDialogBuilder;
    private final LocalyticsController localyticsController;

    @Nullable
    private CompositeSubscription subscriptions;

    private boolean isNeedReloadFeed = true;

    @Inject
    public PersonalPresenter(DataService dataService,
                             ActivityScreenSwitcher screenSwitcher,
                             SharingService sharingService,
                             TokenStorage tokenStorage,
                             OnBoardingDialogBuilder onBoardingDialogBuilder,
                             LocalyticsController localyticsController) {
        this.dataService = dataService;
        this.screenSwitcher = screenSwitcher;
        this.sharingService = sharingService;
        this.onBoardingDialogBuilder = onBoardingDialogBuilder;
        this.tokenStorage = tokenStorage;
        this.localyticsController = localyticsController;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        createCompositeSubscription();

        if (isNeedReloadFeed) {
            isNeedReloadFeed = false;
            reloadFeed();
        }

//        onBoardingDialogBuilder.setCallBack(new OnBoardingDialogBuilder.ChooseDialogCallBack() {
//            @Override
//            public void apply() {
//                tokenStorage.setCreateAlbum(true);
//                localyticsController.showAlbumOnBoarding(LocalyticsController.CREATE);
//            }
//
//            @Override
//            public void cancel() {
//                localyticsController.showAlbumOnBoarding(LocalyticsController.SKIP);
//            }
//        });

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

    private void createCompositeSubscription() {
        if (subscriptions == null) {
            subscriptions = new CompositeSubscription();
        }
    }

    public void reloadFeed() {
        if (!checkView()) {
            isNeedReloadFeed = true;
        } else {
            createCompositeSubscription();
            assert subscriptions != null;
            subscriptions.add(dataService.getMyCollection()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<List<ImageResponse>>() {
                                @Override
                                public void call(List<ImageResponse> imageResponses) {
                                    getView().bindData(imageResponses);
                                }
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.w(throwable, "DataService.getMyCollection() request error!");
                                }
                            }
                    ));
        }
    }

}
