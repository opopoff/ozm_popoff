package com.ozm.rocks.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;
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
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
        private final Merlin merlin;
        private ArrayList<PInfo> mPackages;
        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService, TokenStorage tokenStorage,
                         ActivityScreenSwitcher screenSwitcher, KeyboardPresenter keyboardPresenter,
                         PackageManagerTools packageManagerTools, SharingDialogBuilder sharingDialogBuilder, Merlin
                                 merlin) {
            this.dataService = dataService;
            this.tokenStorage = tokenStorage;
            this.screenSwitcher = screenSwitcher;
            this.keyboardPresenter = keyboardPresenter;
            this.mPackageManagerTools = packageManagerTools;
            this.sharingDialogBuilder = sharingDialogBuilder;
            this.merlin = merlin;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            Timber.e("OnLoad");
            mPackages = mPackageManagerTools.getInstalledPackages();
            subscriptions = new CompositeSubscription();
            subscriptions.add(dataService.sendPackages(mPackages).
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribeOn(Schedulers.io()).
                            subscribe(new Action1<retrofit.client.Response>() {
                                @Override
                                public void call(retrofit.client.Response response) {
                                    Timber.d("Send packages successfully");
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.e(throwable, "Error send packages");
                                }
                            })
            );

            merlin.registerConnectable(new Connectable() {
                @Override
                public void onConnect() {
                    getView().mNoInternetView.setVisibility(View.GONE);
                }
            });
            merlin.registerDisconnectable(new Disconnectable() {
                @Override
                public void onDisconnect() {
                    getView().mNoInternetView.setVisibility(View.VISIBLE);
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

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
        }

        public boolean isLoggedIn() {
            return tokenStorage.isAuthorized();
        }

        public void signIn(String email, String password) {
            final MainView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            subscriptions.add(dataService.signIn(email, password).
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribeOn(Schedulers.io()).
                            subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean signed) {
                                    if (signed) {
                                        Timber.d("Signed in successfully");
                                        final MainView view = getView();
                                        if (view != null) {
                                            keyboardPresenter.hide();
                                            view.openMenu();
                                        }
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.e(throwable, "Error signing in");
                                }
                            })
            );
        }

        public void signOut() {
            final MainView view = getView();
            if (view == null) {
                return;
            }
            tokenStorage.clear();
            view.openLogin();
        }

        public void forgotPassword() {
            // TODO
        }

        public void openScreen(MainScreens screen) {
//            if (screen == MainMenuScreen.ACTIVATION) {
//                screenSwitcher.open(new QrActivationActivity.Screen());
//            }
            // TODO
        }

        public void showSharingDialog() {
            subscriptions.add(dataService.getConfig().
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribeOn(Schedulers.io()).
                            subscribe(new Action1<Config>() {
                                @Override
                                public void call(Config config) {
                                    ArrayList<PInfo> pInfos = new ArrayList<PInfo>();
                                    for (MessengerOrder messengerOrder : config.messengerOrders()) {
                                        for (PInfo pInfo : mPackages) {
                                            if (messengerOrder.applicationId.equals(pInfo.getPname())) {
                                                pInfos.add(pInfo);
                                            }
                                        }
                                    }
                                    sharingDialogBuilder.openDialog(pInfos);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.e(throwable, "Error signing in");
                                }
                            })
            );
        }

        public ArrayList<PInfo> getPackages() {
            return mPackageManagerTools.getInstalledPackages();
        }


        public ArrayList<PInfo> getmPackages() {
            return mPackages;
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
