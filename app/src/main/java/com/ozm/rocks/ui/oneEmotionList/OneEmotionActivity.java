package com.ozm.rocks.ui.oneEmotionList;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreen;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.FileService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;
import com.ozm.rocks.util.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class OneEmotionActivity extends BaseActivity implements HasComponent<OneEmotionComponent> {
    @Inject
    Presenter presenter;

    private long categoryId;
    private String categoryName;
    private OneEmotionComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_U2020);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onExtractParams(@NonNull Bundle params) {
        super.onExtractParams(params);
        categoryId = params.getLong(Screen.BF_CATEGORY);
        categoryName = params.getString(Screen.BF_CATEGORY_NAME);
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerOneEmotionComponent.builder().
                ozomeComponent(ozomeComponent).
                oneEmotionModule(new OneEmotionModule(categoryId, categoryName)).build();
        component.inject(this);
    }

    @Override
    protected void onDestroy() {
        component = null;
        super.onDestroy();
    }

    @Override
    protected int layoutId() {
        return R.layout.one_emotion_layout;
    }

    @Override
    protected BasePresenter<? extends BaseView> presenter() {
        return presenter;
    }

    @Override
    protected int viewId() {
        return R.id.one_emotion_view;
    }

    @Override
    public OneEmotionComponent getComponent() {
        return component;
    }

    @OneEmotionScope
    public static final class Presenter extends BasePresenter<OneEmotionView> {

        private final DataService dataService;
        private final TokenStorage tokenStorage;
        private final ActivityScreenSwitcher screenSwitcher;
        private final KeyboardPresenter keyboardPresenter;
        private final PackageManagerTools mPackageManagerTools;
        private final NetworkState networkState;
        private final SharingDialogBuilder sharingDialogBuilder;
        private final long mCategoryId;
        private final String mCategoryName;
        private ArrayList<PInfo> mPackages;
        @Nullable
        private CompositeSubscription subscriptions;
        private Config mConfig;
        private final Application application;

        @Inject
        public Presenter(DataService dataService, TokenStorage tokenStorage,
                         ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                         PackageManagerTools packageManagerTools, SharingDialogBuilder sharingDialogBuilder,
                         NetworkState networkState, Application application, @Named("category") long categoryId,
                         @Named("categoryName") String categoryName) {
            this.dataService = dataService;
            this.tokenStorage = tokenStorage;
            this.screenSwitcher = screenSwitcher;
            this.keyboardPresenter = keyboardPresenter;
            this.mPackageManagerTools = packageManagerTools;
            this.sharingDialogBuilder = sharingDialogBuilder;
            this.networkState = networkState;
            this.application = application;
            this.mCategoryId = categoryId;
            this.mCategoryName = categoryName;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            mPackages = mPackageManagerTools.getInstalledPackages();
            subscriptions = new CompositeSubscription();
            getView().toolbar.setTitle(mCategoryName);
            networkState.addConnectedListener(new NetworkState.IConnected() {
                @Override
                public void connectedState(boolean isConnected) {
                    showInternetMessage(!isConnected);
                }
            });
//            getView().loadFeed(getView().getLastFromFeedListPosition(), getView().getLastToFeedListPosition());
        }

        public void loadCategoryFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
            final OneEmotionView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getCategoryFeed(mCategoryId, from, to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void updateCategoryFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
            final OneEmotionView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.categoryFeedUpdate(mCategoryId, from, to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }


        public void like(LikeRequest likeRequest, EndlessObserver<String> observer) {
            final OneEmotionView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.like(likeRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void dislike(DislikeRequest dislikeRequest, EndlessObserver<String> observer) {
            final OneEmotionView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.dislike(dislikeRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void saveImage(String url) {
            if (subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.createImage(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        public void hide(HideRequest hideRequest, EndlessObserver<String> observer) {
            final OneEmotionView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.hide(hideRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }


        public void saveImageAndShare(final PInfo pInfo, final ImageResponse image) {
            if (subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.createImage(image.url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            String path = FileService.createDirectory() + Strings.SLASH
                                    + FileService.getFileName(image.url);
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/*");
                            File media = new File(path);
                            Uri uri = Uri.fromFile(media);
                            share.putExtra(Intent.EXTRA_STREAM, uri);
                            share.putExtra(Intent.EXTRA_TEXT, mConfig.replyUrl() + "\n"
                                    + mConfig.replyUrlText());
                            share.setPackage(pInfo.getPname());
                            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            application.startActivity(share);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(String s) {
                        }
                    }));
        }

        public void deleteImage(final ImageResponse image) {
            if (subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.deleteImage(image.url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        public void showSharingDialog(final ImageResponse image) {
            if (subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getConfig().
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribeOn(Schedulers.io()).
                            subscribe(new Action1<Config>() {
                                @Override
                                public void call(Config config) {
                                    mConfig = config;
                                    ArrayList<PInfo> pInfos = new ArrayList<PInfo>();
                                    for (MessengerOrder messengerOrder : config.messengerOrders()) {
                                        for (PInfo pInfo : mPackages) {
                                            if (messengerOrder.applicationId.equals(pInfo.getPname())) {
                                                pInfos.add(pInfo);
                                            }
                                        }
                                    }
                                    sharingDialogBuilder.setCallback(new SharingDialogBuilder.SharingDialogCallBack() {
                                        @Override
                                        public void share(PInfo pInfo, ImageResponse image) {
                                            saveImageAndShare(pInfo, image);
                                        }
                                    });
                                    sharingDialogBuilder.openDialog(pInfos, image);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                }
                            })
            );
        }

        public void showInternetMessage(boolean b) {
//            final OneEmotionView view = getView();
//            if (view == null) {
//                return;
//            }
//            view.mNoInternetView.setVisibility(b ? View.VISIBLE : View.GONE);
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

    public static final class Screen extends ActivityScreen {
        public static final String BF_CATEGORY = "OneEmotionActivity.categoryId";
        public static final String BF_CATEGORY_NAME = "OneEmotionActivity.categoryName";

        private final long categoryId;
        private final String categoryName;

        public Screen(long categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(BF_CATEGORY, categoryId);
            intent.putExtra(BF_CATEGORY_NAME, categoryName);
        }

        @Override
        protected Class<? extends Activity> activityClass() {
            return OneEmotionActivity.class;
        }
    }
}
