package com.ozm.rocks.ui.general;

import android.app.Application;
import android.support.annotation.Nullable;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.GifMessengerOrder;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.categories.OneEmotionView;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PInfo;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@MainScope
public final class MainGeneralPresenter extends BasePresenter<MainGeneralView> {

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
    public MainGeneralPresenter(DataService dataService,
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
        setFirstMessengersInList();

//        loadCategories();
//        networkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
//            @Override
//            public void connectedState(boolean isConnected) {
//                showInternetMessage(!isConnected);
//            }
//        });
    }

    private void setFirstMessengersInList() {
        final MainGeneralView view = getView();
        if (view == null || subscriptions == null) {
            return;
        }
        subscriptions.add(dataService.getConfig().
                observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).
                subscribe(new EndlessObserver<Config>() {
                              @Override
                              public void onNext(Config config) {
                                  mConfig = config;
                                  ArrayList<PInfo> pInfoMessengers = new ArrayList<PInfo>();
                                  ArrayList<PInfo> pInfoGifMessengers = new ArrayList<PInfo>();
                                  for (MessengerOrder messengerOrder : config.messengerOrders()) {
                                      for (PInfo pInfo : sharingService.getPackages()) {
                                          if (messengerOrder.applicationId.equals(pInfo.getPname())) {
                                              pInfoMessengers.add(pInfo);
                                          }
                                      }
                                  }
                                  for (GifMessengerOrder messengerOrder : config.gifMessengerOrders()) {
                                      for (PInfo pInfo : sharingService.getPackages()) {
                                          if (messengerOrder.applicationId.equals(pInfo.getPname())) {
                                              pInfoGifMessengers.add(pInfo);
                                          }
                                      }
                                  }

                                  view.getListAdapter().setMessengers(pInfoMessengers, pInfoGifMessengers);
                              }
                          }
                ));
    }

    public void fastSharing(PInfo pInfo, ImageResponse imageResponse){
        sharingService.saveImageAndShare(pInfo, imageResponse, SharingService.MAIN_FEED);
    }

    public void shareWithDialog(ImageResponse imageResponse) {
        sharingService.showSharingDialog(imageResponse, SharingService.MAIN_FEED);
    }
//    public void loadCategories() {
//        final MainGeneralView view = getView();
//        if (view == null || subscriptions == null) {
//            return;
//        }
//        subscriptions.add(dataService.getCategories()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new EndlessObserver<CategoryResponse>() {
//                    @Override
//                    public void onNext(CategoryResponse response) {
//                        mCategory = response;
//                        view.getEmotionsAdapter().addAll(mCategory.categories, mCategory.promos);
//                    }
//                }));
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharingService.unsubscribe();
        if (subscriptions != null) {
            subscriptions.unsubscribe();
            subscriptions = null;
        }
    }

    public void checkResult() {
        final MainGeneralView view = getView();
        if (view == null) {
            return;
        }
        view.getListAdapter().update(mLikeHideResult, new EndlessObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                mLikeHideResult.clearResult();
                view.getListAdapter().notifyDataSetChanged();
            }
        });
    }
}
