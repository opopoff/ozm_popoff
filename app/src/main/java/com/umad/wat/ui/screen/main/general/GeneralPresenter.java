package com.umad.wat.ui.screen.main.general;

import com.umad.wat.base.mvp.BasePresenter;
import com.umad.wat.data.DataService;
import com.umad.wat.data.SharingService;
import com.umad.wat.data.TokenStorage;
import com.umad.wat.data.api.response.CategoryResponse;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.rx.EndlessObserver;
import com.umad.wat.ui.OnGoBackInterface;
import com.umad.wat.ui.OnGoBackPresenter;
import com.umad.wat.ui.screen.main.MainScope;
import com.umad.wat.util.NetworkState;
import com.umad.wat.util.PInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
        subscriptions.add(dataService.getPInfos(false, true)
                .flatMap(new Func1<ArrayList<PInfo>, Observable<ArrayList<PInfo>>>() {
                    @Override
                    public Observable<ArrayList<PInfo>> call(ArrayList<PInfo> pInfos) {
                        view.setMessengers(pInfos, false);
                        return dataService.getPInfos(true, true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<ArrayList<PInfo>>() {
                            @Override
                            public void call(ArrayList<PInfo> pInfos) {
                                view.setMessengers(pInfos, true);
                                view.showContent();
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        }
                ));
    }

    public void loadGeneralFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
        if (!checkView()) return;
        final GeneralView view = getView();
        if (view == null || subscriptions == null) {
            return;
        }
        subscriptions.add(dataService.getGeneralFeed(from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer));
    }

    public void updateGeneralFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
        final GeneralView view = getView();
        if (view == null || subscriptions == null) {
            return;
        }
        subscriptions.add(dataService.generalFeedUpdate(from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer));
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
        subscriptions.add(sharingService.saveImageFromCacheAndShare(pInfo, imageResponse,
                SharingService.GENERAL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void shareWithDialog(ImageResponse imageResponse) {
        subscriptions.add(sharingService.shareWithStandardChooser(imageResponse)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
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
}
