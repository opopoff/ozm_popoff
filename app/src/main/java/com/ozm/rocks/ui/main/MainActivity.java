package com.ozm.rocks.ui.main;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

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
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.GifMessengerOrder;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.MessengerConfigs;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.emotionList.OneEmotionActivity;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;
import com.ozm.rocks.util.Strings;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements HasComponent<MainComponent> {
    @Inject
    Presenter presenter;

    @Inject
    SharingDialogBuilder sharingDialogBuilder;
    private MainComponent component;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_U2020);
        super.onCreate(savedInstanceState);
        sharingDialogBuilder.attach(this);

    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerMainComponent.builder().
                ozomeComponent(ozomeComponent).build();
        component.inject(this);
    }

    @Override
    protected void onDestroy() {
        component = null;
        super.onDestroy();
    }

    @Override
    protected int layoutId() {
        return R.layout.main_layout;
    }

    @Override
    protected BasePresenter<? extends BaseView> presenter() {
        return presenter;
    }

    @Override
    protected int viewId() {
        return R.id.main_view;
    }

    @Override
    public MainComponent getComponent() {
        return component;
    }

    @MainScope
    public static final class Presenter extends BasePresenter<MainView> {

        private final DataService dataService;
        private final TokenStorage tokenStorage;
        private final ActivityScreenSwitcher screenSwitcher;
        private final SharingDialogBuilder sharingDialogBuilder;
        private final KeyboardPresenter keyboardPresenter;
        private final PackageManagerTools mPackageManagerTools;
        private final NetworkState networkState;
        private ArrayList<PInfo> mPackages;
        private final Application application;
        private Config mConfig;

        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService, TokenStorage tokenStorage,
                         ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                         PackageManagerTools packageManagerTools, SharingDialogBuilder sharingDialogBuilder,
                         NetworkState networkState, Application application) {
            this.dataService = dataService;
            this.tokenStorage = tokenStorage;
            this.screenSwitcher = screenSwitcher;
            this.keyboardPresenter = keyboardPresenter;
            this.mPackageManagerTools = packageManagerTools;
            this.sharingDialogBuilder = sharingDialogBuilder;
            this.application = application;
            this.networkState = networkState;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            Timber.e("OnLoad");

            //TODO transition to LoadingActivity send package
            mPackages = mPackageManagerTools.getInstalledPackages();
            subscriptions = new CompositeSubscription();
            subscriptions.add(dataService.sendPackages(mPackages).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribeOn(Schedulers.io()).
                    subscribe());
            networkState.addConnectedListener(new NetworkState.IConnected() {
                @Override
                public void connectedState(boolean isConnected) {
                    showInternetMessage(!isConnected);
                }
            });
        }

        public void loadGeneralFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getGeneralFeed(from, to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void updateGeneralFeed(int from, int to, EndlessObserver<List<ImageResponse>> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.generalFeedUpdate(from, to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void loadMyCollection(EndlessObserver<List<ImageResponse>> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.getMyCollection()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void like(LikeRequest likeRequest, EndlessObserver<String> observer) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.like(likeRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer));
        }

        public void dislike(DislikeRequest dislikeRequest, EndlessObserver<String> observer) {
            final MainView view = getView();
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
            final MainView view = getView();
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
                            MessengerConfigs currentMessengerConfigs = null;
                            for (MessengerConfigs messengerConfigs : mConfig.messengerConfigs()) {
                                for (PInfo pInfo : mPackages) {
                                    if (messengerConfigs.applicationId.equals(pInfo.getPname())) {
                                        currentMessengerConfigs = messengerConfigs;
                                    }
                                }
                            }
                            String type = "text/plain";
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType(type);
                            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            share.setPackage(pInfo.getPname());
                            if (currentMessengerConfigs != null) {
                                if (currentMessengerConfigs.supportsImageTextReply) {
                                    share.putExtra(Intent.EXTRA_TEXT, image.url + Strings.ENTER
                                            + mConfig.replyUrl() + Strings.ENTER
                                            + mConfig.replyUrlText());
                                } else if (currentMessengerConfigs.supportsImageReply) {
                                    share.putExtra(Intent.EXTRA_TEXT, image.sharingUrl);
                                }
                            }
                            application.startActivity(share);
//                                String path = FileService.createDirectory() + Strings.SLASH
//                                        + FileService.getFileName(image.url);
//                                Intent share = new Intent(Intent.ACTION_SEND);
//                                share.setType("image/*");
//                                File media = new File(path);
//                                Uri uri = Uri.fromFile(media);
//                                share.putExtra(Intent.EXTRA_STREAM, uri);
//                                share.putExtra(Intent.EXTRA_TEXT, mConfig.replyUrl() + "\n"
//                                        + mConfig.replyUrlText());
//                                share.setPackage(pInfo.getPname());
//                                share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                application.startActivity(share);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(String s) {
                        }
                    }));
        }

        private void shareOther(ImageResponse imageResponse) {
            String type;
            if (imageResponse.isGIF) {
                type = "image/gif";
            } else {
                type = "image/*";
            }
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType(type);
            share.putExtra(Intent.EXTRA_TEXT, imageResponse.url + Strings.ENTER
                    + mConfig.replyUrl() + Strings.ENTER
                    + mConfig.replyUrlText());
            Intent chooser = Intent.createChooser(share, "Share to");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(chooser);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriptions != null) {
            subscriptions.unsubscribe();
            subscriptions = null;
        }
    }

    public void openScreen(MainScreens screen) {
//            if (screen == MainMenuScreen.ACTIVATION) {
//                screenSwitcher.open(new QrActivationActivity.Screen());
//            }
        // TODO
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
                                if (image.isGIF) {
                                    for (GifMessengerOrder gifMessengerOrder : config.gifMessengerOrders()) {
                                        for (PInfo pInfo : mPackages) {
                                            if (gifMessengerOrder.applicationId.equals(pInfo.getPname())) {
                                                pInfos.add(pInfo);
                                            }
                                        }
                                    }
                                } else {
                                    for (MessengerOrder messengerOrder : config.messengerOrders()) {
                                        for (PInfo pInfo : mPackages) {
                                            if (messengerOrder.applicationId.equals(pInfo.getPname())) {
                                                pInfos.add(pInfo);
                                            }
                                        }
                                    }
                                }
                                sharingDialogBuilder.setCallback(new SharingDialogBuilder.SharingDialogCallBack() {
                                    @Override
                                    public void share(PInfo pInfo, ImageResponse image) {
                                        saveImageAndShare(pInfo, image);
                                    }

                                    @Override
                                    public void hideImage(ImageResponse imageResponse) {
                                    }

                                    @Override
                                    public void other(ImageResponse imageResponse) {
                                        shareOther(imageResponse);
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
        final MainView view = getView();
        if (view == null) {
            return;
        }
        view.mNoInternetView.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public void openOneEmotionScreen(long categoryId, String categoryName) {
        screenSwitcher.open(new OneEmotionActivity.Screen(categoryId, categoryName));
    }
}

public static final class Screen extends ActivityScreen {
    @Override
    protected void configureIntent(@NonNull Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    protected Class<? extends Activity> activityClass() {
        return MainActivity.class;
    }
}
}
