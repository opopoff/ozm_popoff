package com.ozm.fun.ui.screen.main.general;

import com.ozm.fun.base.mvp.BasePresenter;
import com.ozm.fun.data.DataService;
import com.ozm.fun.data.SharingService;
import com.ozm.fun.data.TokenStorage;
import com.ozm.fun.data.api.model.Config;
import com.ozm.fun.data.api.response.CategoryResponse;
import com.ozm.fun.data.api.response.GifMessengerOrder;
import com.ozm.fun.data.api.response.ImageResponse;
import com.ozm.fun.data.api.response.MessengerOrder;
import com.ozm.fun.data.rx.EndlessObserver;
import com.ozm.fun.ui.OnGoBackInterface;
import com.ozm.fun.ui.OnGoBackPresenter;
import com.ozm.fun.ui.screen.main.MainScope;
import com.ozm.fun.util.NetworkState;
import com.ozm.fun.util.PInfo;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@MainScope
public final class GeneralPresenter extends BasePresenter<GeneralView> {
    private static final String KEY_LISTENER = "GeneralPresenter";

    private final DataService dataService;
    private final SharingService sharingService;
    private final OnGoBackPresenter onGoBackPresenter;
    private final TokenStorage tokenStorage;

    private CompositeSubscription subscriptions;
    private CategoryResponse mCategory;
    private NetworkState networkState;

    @Inject
    public GeneralPresenter(DataService dataService, SharingService sharingService,
                            OnGoBackPresenter onGoBackPresenter,
                            NetworkState networkState, TokenStorage tokenStorage) {
        this.dataService = dataService;
        this.sharingService = sharingService;
        this.onGoBackPresenter = onGoBackPresenter;
        this.networkState = networkState;
        this.tokenStorage = tokenStorage;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        subscriptions = new CompositeSubscription();
        setFirstMessengersInList();
        loadCategories();
        networkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
            @Override
            public void connectedState(boolean isConnected) {
                if (checkView()) {
                    getView().loadFeedFromNetworkState(isConnected);
                }
            }
        });
    }

    public void likeDislike(ImageResponse imageResponse) {
        final GeneralView view = getView();
        if (view == null || subscriptions == null) {
            return;
        }
//        sharingService.sendActionLikeDislike(SharingService.MAIN_FEED, imageResponse);

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
                                  ArrayList<PInfo> pInfoMessengers = new ArrayList<PInfo>();
                                  ArrayList<PInfo> pInfoGifMessengers = new ArrayList<PInfo>();
                                  for (MessengerOrder messengerOrder : config.messengerOrders()) {
//                                      for (PInfo pInfo : sharingService.getPackages()) {
//                                          if (messengerOrder.applicationId.equals(pInfo.getPackageName())) {
//                                              pInfoMessengers.add(pInfo);
//                                          }
//                                      }
                                  }
                                  for (GifMessengerOrder messengerOrder : config.gifMessengerOrders()) {
//                                      for (PInfo pInfo : sharingService.getPackages()) {
//                                          if (messengerOrder.applicationId.equals(pInfo.getPackageName())) {
//                                              pInfoGifMessengers.add(pInfo);
//                                          }
//                                      }
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

    public void fastSharing(PInfo pInfo, ImageResponse imageResponse) {
//        sharingService.saveImageAndShare(pInfo, imageResponse, SharingService.MAIN_FEED);
    }

    public void shareWithDialog(ImageResponse imageResponse) {
//        sharingService.showSharingDialog(imageResponse, SharingService.MAIN_FEED);
    }

    public void onBoarding() {
        if (!tokenStorage.isFeedPromptShowed()) {
            tokenStorage.setFeedPromptShowed();
            onGoBackPresenter.setOnGoBackInterface(new OnGoBackInterface() {
                @Override
                public void onBack() {
                    onGoBackPresenter.setOnGoBackInterface(null);
                    hideOnBoarding();
                }
            });
            if (checkView()) {
                getView().showOnBoardingMessage();
            }
        }
    }

    public void hideOnBoarding() {
        if (checkView()) {
            getView().hideOnBoardingMessage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharingService.unsubscribe();
        networkState.deleteConnectedListener(KEY_LISTENER);
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
//        view.getListAdapter().update(mLikeHideResult, new EndlessObserver<Boolean>() {
//            @Override
//            public void onNext(Boolean aBoolean) {
//                mLikeHideResult.clearResult();
//                view.getListAdapter().notifyDataSetChanged();
//            }
//        });
    }
}