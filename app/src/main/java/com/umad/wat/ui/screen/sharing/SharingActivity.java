package com.umad.wat.ui.screen.sharing;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.umad.R;
import com.umad.wat.OzomeComponent;
import com.umad.wat.base.HasComponent;
import com.umad.wat.base.mvp.BasePresenter;
import com.umad.wat.base.mvp.BaseView;
import com.umad.wat.base.navigation.activity.ActivityScreen;
import com.umad.wat.base.navigation.activity.ActivityScreenSwitcher;
import com.umad.wat.base.tools.ToastPresenter;
import com.umad.wat.data.DataService;
import com.umad.wat.data.RequestResultCodes;
import com.umad.wat.data.SharingService;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.api.response.PackageRequest;
import com.umad.wat.data.social.SocialActivity;
import com.umad.wat.data.model.PInfo;
import com.umad.wat.util.PackageManagerTools;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SharingActivity extends SocialActivity implements HasComponent<SharingComponent> {

    @Inject Presenter presenter;

    private ImageResponse imageResponse;
    private int from;
    private SharingComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onExtractParams(@NonNull Bundle params) {
        super.onExtractParams(params);
        imageResponse = params.getParcelable(Screen.BF_IMAGE);
        from = params.getInt(Screen.BF_FROM);
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerSharingComponent.builder().
                ozomeComponent(ozomeComponent).
                sharingModule(new SharingModule(imageResponse, from)).build();
        component.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        component = null;
        imageResponse = null;
    }

    @Override
    protected int layoutId() {
        return R.layout.sharing_view;
    }

    @Override
    protected BasePresenter<? extends BaseView> presenter() {
        return presenter;
    }

    @Override
    protected int viewId() {
        return R.id.sharing_view;
    }

    @Override
    public SharingComponent getComponent() {
        return component;
    }

    @Override
    public String uniqueKey() {
        return String.valueOf(imageResponse.id);
    }

    @SharingScope
    public static final class Presenter extends BasePresenter<SharingView> {
        private static final String DIALOGS_VK_URL = "http://vk.com/im";
        private static final String SR_IMAGE_KEY = "SharingActivity.image";
        private static final String SR_FROM_KEY = "SharingActivity.from";
        private static final String SR_PACKAGES_KEY = "SharingActivity.packages";
        private static final String SR_VIEW_PACKAGES_KEY = "SharingActivity.viewPackages";
        private static final String SR_IS_SHARED_KEY = "SharingActivity.isShared";

        private final DataService dataService;
        private final ActivityScreenSwitcher screenSwitcher;
        private final Application application;
        private final SharingService sharingService;
        private final ToastPresenter toastPresenter;

        private int from;
        private ImageResponse imageResponse;
        private ArrayList<PInfo> viewPackages;

        private Boolean isShared = false;

        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService,
                         ActivityScreenSwitcher screenSwitcher,
                         SharingService sharingService,
                         @Named(SharingModule.MP_IMAGE) ImageResponse imageResponse,
                         @Named(SharingModule.MP_FROM) int from,
                         Application application,
                         ToastPresenter toastPresenter) {
            this.dataService = dataService;
            this.screenSwitcher = screenSwitcher;
            this.sharingService = sharingService;
            this.imageResponse = imageResponse;
            this.from = from;
            this.application = application;
            this.toastPresenter = toastPresenter;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            Timber.d("SharingActivity: onLoad()");
            subscriptions = new CompositeSubscription();
            getViewPackages();
            if (isShared) {
                Intent data = new Intent();
                data.putExtra(RequestResultCodes.IMAGE_RESPONSE_KEY, imageResponse);
                screenSwitcher.setResult(RequestResultCodes.RESULT_CODE_SHARE_IMAGE, data);
            }
        }

        private void getViewPackages() {
            if (subscriptions == null) {
                Timber.d("SharingActivity: subscriptions null");
                subscriptions = new CompositeSubscription();
            }
            Timber.d("SharingActivity: subscriptions notnull");
            if (viewPackages != null && imageResponse != null) {
                getView().setData(new ArrayList<>(viewPackages));
                return;
            }
            subscriptions.add(dataService.getPackages().flatMap(
                    new Func1<ArrayList<PInfo>, Observable<ArrayList<PInfo>>>() {
                        @Override
                        public Observable<ArrayList<PInfo>> call(ArrayList<PInfo> pInfos) {
                            getView().setVisibilityVkFb(pInfos);
                            return dataService.getPInfos(imageResponse.isGIF, false);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<ArrayList<PInfo>>() {
                                @Override
                                public void call(ArrayList<PInfo> pInfos) {
                                    viewPackages = pInfos;
                                    getView().setData(new ArrayList<>(pInfos));
                                }
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.d(throwable, "SharingActivity getViewPackages:");
                                }
                            }));
        }

        public void sendPackages(PackageRequest.VkData vkData) {
            // TODO take something with vk profile information;
//            dataService.sendPackages(vkData);
        }

        public void share(PInfo pInfo) {
            if (subscriptions == null) {
                subscriptions = new CompositeSubscription();
            }
            subscriptions.add(sharingService.saveImageFromCacheAndShare(pInfo, imageResponse, from)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Timber.d(throwable, "SharingActivity share:");
                        }
                    }));
            isShared = true;
            toastPresenter.show(R.string.sharing_view_toast_message, Toast.LENGTH_SHORT);
        }

        public void shareVK(final VKApiUser user, boolean sendLinkToVk) {
            VKRequest.VKRequestListener vkRequestListener = new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    if (checkView()) {
                        getView().notifyVkAdapter();
                    }
                    Intent startBrowser = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(DIALOGS_VK_URL));
                    startBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    application.startActivity(startBrowser);
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    Timber.d("SharingActivity: shareVK onError %s", error.toString());
                    Toast.makeText(application, R.string.error_information_repeate_please, Toast.LENGTH_SHORT).show();
                }
            };
            if (subscriptions == null) {
                subscriptions = new CompositeSubscription();
            }
            subscriptions.add(sharingService.shareToVk(imageResponse, user, vkRequestListener, from, sendLinkToVk)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            Timber.d("SharingActivity: shareToVk call %b", aBoolean);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Timber.d("SharingActivity: shareToVk call throwable %s", throwable.toString());
                            Toast.makeText(application, R.string.error_information_repeate_please, Toast.LENGTH_SHORT).show();
                        }
                    }));
            isShared = true;
        }

        public void shareFB() {
            if (subscriptions == null) {
                subscriptions = new CompositeSubscription();
            }
            subscriptions.add(sharingService.shareToFb(imageResponse, from)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Timber.d(throwable, "SharingActivity shareFB:");
                        }
                    }));
            isShared = true;
            toastPresenter.show(R.string.sharing_view_toast_message, Toast.LENGTH_SHORT);
        }

        public void shareVKAll() {
            if (subscriptions == null) {
                subscriptions = new CompositeSubscription();
            }
            subscriptions.add(
                    dataService.getPackages().flatMap(new Func1<ArrayList<PInfo>, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(ArrayList<PInfo> pInfos) {
                            for (PInfo pInfo : pInfos) {
                                if (pInfo.getPackageName().equals(PackageManagerTools.Messanger.VKONTAKTE.getPackagename())) {
                                    return sharingService.saveImageFromCacheAndShare(pInfo, imageResponse, from);
                                }
                            }
                            return null;
                        }
                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Timber.d(throwable, "SharingActivity shareVKAll:");
                                }
                            }));

            isShared = true;
        }

        public void shareOther() {
            sharingService.shareWithChooser(imageResponse, from);
            isShared = true;
            toastPresenter.show(R.string.sharing_view_toast_message, Toast.LENGTH_SHORT);
        }

        public void like() {
            sharingService.sendActionLikeDislike(from, imageResponse);
            Intent data = new Intent();
            data.putExtra(RequestResultCodes.IMAGE_RESPONSE_KEY, imageResponse);
            screenSwitcher.setResult(RequestResultCodes.RESULT_CODE_LIKE_IMAGE, data);
        }

        public void hide() {
            sharingService.sendActionHide(from, imageResponse);
            Intent data = new Intent();
            data.putExtra(RequestResultCodes.IMAGE_RESPONSE_KEY, imageResponse);
            screenSwitcher.goBackResult(RequestResultCodes.RESULT_CODE_HIDE_IMAGE, data);
        }

        public ImageResponse getImageResponse() {
            return imageResponse;
        }

        @Override
        protected void onRestore(@NonNull Bundle savedInstanceState) {
            super.onRestore(savedInstanceState);
            imageResponse = savedInstanceState.getParcelable(SR_IMAGE_KEY);
            from = savedInstanceState.getInt(SR_FROM_KEY);
            viewPackages = savedInstanceState.getParcelable(SR_VIEW_PACKAGES_KEY);
            isShared = savedInstanceState.getBoolean(SR_IS_SHARED_KEY);
        }

        @Override
        protected void onSave(@NonNull Bundle outState) {
            outState.putParcelable(SR_IMAGE_KEY, imageResponse);
            outState.putInt(SR_FROM_KEY, from);
            outState.putParcelableArrayList(SR_VIEW_PACKAGES_KEY, viewPackages);
            outState.putBoolean(SR_IS_SHARED_KEY, isShared);
            super.onSave(outState);
        }

        @Override
        protected void onDestroy() {
            Timber.d("SharingActivity: onDestroy()");
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
            super.onDestroy();
        }

    }

    public static final class Screen extends ActivityScreen {
        public static final String BF_IMAGE = "SharingActivity.image";
        public static final String BF_FROM = "SharingActivity.from";
        private final ImageResponse imageResponse;
        private final int from;

        public Screen(ImageResponse imageResponse, @SharingService.From int from) {
            this.imageResponse = imageResponse;
            this.from = from;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(BF_IMAGE, imageResponse);
            intent.putExtra(BF_FROM, from);
        }

        @Override
        protected Bundle activityOptions(Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return ActivityOptions.makeCustomAnimation(activity.getApplicationContext(),
                        R.anim.sharing_view_in, R.anim.sharing_view_out).toBundle();
            } else {
                return super.activityOptions(activity);
            }
        }

        @Override
        protected Class<? extends Activity> activityClass() {
            return SharingActivity.class;
        }
    }
}
