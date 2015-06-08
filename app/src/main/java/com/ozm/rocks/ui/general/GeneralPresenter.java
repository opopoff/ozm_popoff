package com.ozm.rocks.ui.general;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

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
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PInfo;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@MainScope
public final class GeneralPresenter extends BasePresenter<GeneralView> {

    private final DataService dataService;
    private final ActivityScreenSwitcher screenSwitcher;
    private final SharingService sharingService;
    private final KeyboardPresenter keyboardPresenter;
    private final NetworkState networkState;
    private final Application application;
    private final LikeHideResult mLikeHideResult;
    private Config mConfig;

    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;

    @Inject
    public GeneralPresenter(DataService dataService,
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
        loadCategories();
    }

    private void setFirstMessengersInList() {
        final GeneralView view = getView();
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
                                          if (messengerOrder.applicationId.equals(pInfo.getPackageName())) {
                                              pInfoMessengers.add(pInfo);
                                          }
                                      }
                                  }
                                  for (GifMessengerOrder messengerOrder : config.gifMessengerOrders()) {
                                      for (PInfo pInfo : sharingService.getPackages()) {
                                          if (messengerOrder.applicationId.equals(pInfo.getPackageName())) {
                                              pInfoGifMessengers.add(pInfo);
                                          }
                                      }
                                  }

                                  view.getListAdapter().setMessengers(pInfoMessengers, pInfoGifMessengers);
                                  view.showContent();
                              }
                          }
                ));
    }

    public void loadCategories() {
        if (mCategory != null) {
            bindCategoryToView();
            return;
        }
        subscriptions.add(dataService.getCategories().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribeOn(Schedulers.io()).
                        subscribe(
                                new Action1<CategoryResponse>() {
                                    @Override
                                    public void call(CategoryResponse categoryResponse) {
                                        mCategory = categoryResponse;
                                        bindCategoryToView();
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Timber.e(throwable, "Getting of CategoryResponse problem!");
                                    }
                                }
                        )
        );
    }

    public void bindCategoryToView() {
        final GeneralView view = getView();
        if (view == null || mCategory == null) return;
        view.bindCategory(mCategory);
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
        final GeneralView view = getView();
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
