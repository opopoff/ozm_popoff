package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreen;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.social.SocialActivity;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;
import com.ozm.rocks.util.Timestamp;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SharingActivity extends SocialActivity implements HasComponent<SharingComponent> {
    @Inject
    Presenter presenter;

    @Inject
    SharingDialogBuilder sharingDialogBuilder;

    @Inject
    ChooseDialogBuilder chooseDialogBuilder;

    private ImageResponse imageResponse;
    private int from;
    private SharingComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
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
    protected void onStart() {
        super.onStart();
        sharingDialogBuilder.attach(this);
        chooseDialogBuilder.attach(this);
    }

    @Override
    protected void onStop() {
        sharingDialogBuilder.detach();
        chooseDialogBuilder.detach();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        component = null;
        super.onDestroy();
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

    @SharingScope
    public static final class Presenter extends BasePresenter<SharingView> {
        private final DataService dataService;
        private final ActivityScreenSwitcher screenSwitcher;
        private final ImageResponse imageResponse;
        private final SharingService sharingService;
        private final LocalyticsController localyticsController;
        private final ChooseDialogBuilder chooseDialogBuilder;
        private final int from;
        private ArrayList<PInfo> packages;

        @Nullable
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(DataService dataService, ActivityScreenSwitcher screenSwitcher,
                         SharingService sharingService, @Named("sharingImage") ImageResponse imageResponse,
                         LocalyticsController localyticsController, @Named("sharingFrom") int from,
                         ChooseDialogBuilder chooseDialogBuilder) {
            this.dataService = dataService;
            this.screenSwitcher = screenSwitcher;
            this.sharingService = sharingService;
            this.imageResponse = imageResponse;
            this.localyticsController = localyticsController;
            this.from = from;
            this.chooseDialogBuilder = chooseDialogBuilder;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();
            getPackages();
        }

        private void getPackages() {
            final SharingView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            if (packages != null && imageResponse != null) {
                getView().setData(imageResponse, (ArrayList<PInfo>) packages.clone());
                return;
            }
            subscriptions.add(dataService.getPackages()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ArrayList<PInfo>>() {
                        @Override
                        public void call(ArrayList<PInfo> pInfos) {
                            packages = pInfos;
                            getView().setData(imageResponse, (ArrayList<PInfo>) pInfos.clone());
                        }
                    }));
        }

        public void share(PInfo pInfo) {
            sharingService.saveImageAndShare(pInfo, imageResponse, from);
        }

        public void shareVK(VKApiUser user, VKRequest.VKRequestListener vkRequestListener) {
            sharingService.shareVK(imageResponse, user, vkRequestListener);
        }

        public void shareFB() {
            for (PInfo pInfo : packages) {
                if (pInfo.getPackageName().equals(PackageManagerTools.FB_MESSENGER_PACKAGE)) {
                    sharingService.saveImageAndShare(pInfo, imageResponse, from);
                    break;
                }
            }
        }

        public void shareOther() {
            chooseDialogBuilder.setCallback(new ChooseDialogBuilder.ChooseDialogCallBack() {
                @Override
                public void share(PInfo pInfo, ImageResponse imageResponse) {
                    localyticsController.shareOutside(pInfo.getApplicationName());
                    sharingService.saveImageAndShare(pInfo, imageResponse, from);
                }
            });
            chooseDialogBuilder.openDialog(packages, imageResponse);
        }

        public void like() {
            final SharingView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            localyticsController.like(imageResponse.isGIF ? LocalyticsController.GIF : LocalyticsController.JPEG);
            ArrayList<Action> actions = new ArrayList<>();
            switch (from) {
                case SharingService.PERSONAL:
                    localyticsController.share(LocalyticsController.FAVORITES);
                    actions.add(Action.getLikeDislikeHideActionForPersonal(imageResponse.id, Timestamp.getUTC()));
                    break;
                case SharingService.CATEGORY_FEED:
                    localyticsController.share(LocalyticsController.FEED);
                    actions.add(Action.getLikeDislikeHideAction(imageResponse.id, Timestamp.getUTC(),
                            imageResponse.categoryId));
                    break;
                case SharingService.GOLD_CATEGORY_FEED:
                    localyticsController.share(LocalyticsController.LIBRARY);
                    actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(imageResponse.id, Timestamp.getUTC(),
                            imageResponse.categoryId));
                    break;
                default:
                case SharingService.MAIN_FEED:
                    localyticsController.share(LocalyticsController.FEED);
                    actions.add(Action.getLikeDislikeHideActionForMainFeed(imageResponse.id, Timestamp.getUTC()));
                    break;
            }
            if (imageResponse.liked) {
                subscriptions.add(dataService.deleteImage(imageResponse.url).
                        mergeWith(dataService.deleteImage(imageResponse.sharingUrl))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
                subscriptions.add(dataService.dislike(new DislikeRequest(actions))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
            } else {
                subscriptions.add(dataService.createImage(imageResponse.url, imageResponse.sharingUrl)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
                subscriptions.add(dataService.like(new LikeRequest(actions))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
            }
        }

        public void hide() {
            final SharingView view = getView();
            if (view == null || subscriptions == null) {
                return;
            }
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(imageResponse.id,
                    Timestamp.getUTC(), imageResponse.categoryId));
            subscriptions.add(dataService.hide(new HideRequest(actions))
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
        protected Class<? extends Activity> activityClass() {
            return SharingActivity.class;
        }
    }
}
