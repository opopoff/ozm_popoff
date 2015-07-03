package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.ozm.R;
import com.ozm.rocks.base.ActivityConnector;
import com.ozm.rocks.base.tools.ToastPresenter;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.FileService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.request.ShareRequest;
import com.ozm.rocks.data.api.response.GifMessengerOrder;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.MessengerConfigs;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.data.api.response.PackageRequest;
import com.ozm.rocks.data.rx.RequestFunction;
import com.ozm.rocks.data.social.SocialPresenter;
import com.ozm.rocks.data.social.dialog.ApiVkDialogResponse;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;
import com.ozm.rocks.util.Strings;
import com.ozm.rocks.util.Timestamp;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.photo.VKUploadMessagesPhotoRequest;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Danil on 22.05.2015.
 */
@ApplicationScope
public class SharingService extends ActivityConnector<Activity> {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PERSONAL, MAIN_FEED, CATEGORY_FEED, GOLD_FAVORITES, GOLD_NOVELTY})
    public @interface From {

    }

    // TODO (a.m.) need remove;
    public static final int MAIN_FEED = -1;
    public static final int CATEGORY_FEED = -2;

    public static final int PERSONAL = 1;
    public static final int GOLD_FAVORITES = 2;
    public static final int GOLD_NOVELTY = 3;

    private final DataService dataService;
    private final LocalyticsController localyticsController;
    private final SharingDialogBuilder sharingDialogBuilder;
    private final ChooseDialogBuilder chooseDialogBuilder;
    private final Picasso picasso;
    private final SocialPresenter socialPresenter;
    private final TokenStorage tokenStorage;
    private final ToastPresenter toastPresenter;

    private Config config;
    private ArrayList<PInfo> packages;

    @Nullable
    private CompositeSubscription subscriptions;

    @Inject
    public SharingService(DataService dataService,
                          SharingDialogBuilder sharingDialogBuilder,
                          ChooseDialogBuilder chooseDialogBuilder,
                          LocalyticsController localyticsController, Picasso picasso,
                          ToastPresenter toastPresenter,
                          SocialPresenter socialPresenter, TokenStorage tokenStorage) {
        this.dataService = dataService;
        this.sharingDialogBuilder = sharingDialogBuilder;
        this.chooseDialogBuilder = chooseDialogBuilder;
        this.localyticsController = localyticsController;
        this.picasso = picasso;
        this.socialPresenter = socialPresenter;
        this.tokenStorage = tokenStorage;
        this.toastPresenter = toastPresenter;
        subscriptions = new CompositeSubscription();
    }

    public void sendPackages(final PackageRequest.VkData vkData, final Action1 action1) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }

        subscriptions.add(dataService.getPackages()
                        .flatMap(new Func1<ArrayList<PInfo>, Observable<Response>>() {
                            @Override
                            public Observable<Response> call(ArrayList<PInfo> pInfos) {
                                packages = new ArrayList<PInfo>(pInfos);
                                return dataService.sendPackages(pInfos, vkData);
                            }
                        })
                        .flatMap(new Func1<Response, Observable<Config>>() {
                            @Override
                            public Observable<Config> call(Response response) {
                                return dataService.getConfig();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                new Action1<Config>() {
                                    @Override
                                    public void call(Config config) {
                                        SharingService.this.config = config;
                                        action1.call(true);
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        // TODO (d.p.) something;
                                        action1.call(false);
                                    }
                                }
                        )
        );

    }

    public ArrayList<PInfo> getPackages() {
        return packages;
    }


    public void showSharingDialog(final ImageResponse image, @From final int from) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        subscriptions.add(
                dataService.getPackages()
                        .flatMap(new Func1<ArrayList<PInfo>, Observable<Config>>() {
                            @Override
                            public Observable<Config> call(ArrayList<PInfo> pInfos) {
                                packages = new ArrayList<>(pInfos);
                                return dataService.getConfig();
                            }
                        }).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribeOn(Schedulers.io()).
                        subscribe(new Action1<Config>() {
                            @Override
                            public void call(Config config) {
                                SharingService.this.config = config;
                                ArrayList<PInfo> pInfos = new ArrayList<>();
                                if (image.isGIF) {
                                    for (GifMessengerOrder gifMessengerOrder : config.gifMessengerOrders()) {
                                        for (PInfo pInfo : packages) {
                                            if (gifMessengerOrder.applicationId.equals(pInfo.getPackageName())) {
                                                pInfos.add(pInfo);
                                            }
                                        }
                                    }
                                } else {
                                    for (MessengerOrder messengerOrder : config.messengerOrders()) {
                                        for (PInfo pInfo : packages) {
                                            if (messengerOrder.applicationId.equals(pInfo.getPackageName())) {
                                                pInfos.add(pInfo);
                                            }
                                        }
                                    }
                                }
                                sharingDialogBuilder.setCallback(new SharingDialogBuilder.SharingDialogCallBack() {
                                    @Override
                                    public void share(PInfo pInfo, ImageResponse image) {
                                        localyticsController.shareOutside(pInfo.getApplicationName());
                                        saveImageAndShare(pInfo, image, from);
                                    }

                                    @Override
                                    public void shareVK(ImageResponse imageResponse, VKApiUser user,
                                                        VKRequest.VKRequestListener vkRequestListener) {
                                        SharingService.this.shareVK(imageResponse, user, vkRequestListener);
                                    }

                                    @Override
                                    public void hideImage(ImageResponse imageResponse) {
                                        sendActionHide(from, imageResponse);
                                    }

                                    @Override
                                    public void other(ImageResponse imageResponse) {
                                        chooseDialogBuilder.setCallback(new ChooseDialogBuilder.ChooseDialogCallBack() {
                                            @Override
                                            public void share(PInfo pInfo, ImageResponse imageResponse) {
                                                localyticsController.shareOutside(pInfo.getApplicationName());
                                                saveImageAndShare(pInfo, imageResponse, from);
                                            }
                                        });
                                        chooseDialogBuilder.openDialog(packages, imageResponse);
                                    }
                                });
                                sharingDialogBuilder.openDialog(pInfos, image, picasso,
                                        socialPresenter, SharingService.this);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.e("Sharing Service error");
                            }
                        })
        );
    }

    public void saveImageAndShare(final PInfo pInfo,
                                  final ImageResponse image,
                                  @From final int from) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        subscriptions.add(dataService.createImage(image.url, image.sharingUrl, image.imageType)
                        .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Boolean aBoolean) {
                                return dataService.createVideo(image.videoUrl);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean isSave) {
                                        share(pInfo, image, from);
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Timber.e(throwable, "Save image");
                                    }
                                }
                        )
        );
    }

    public Observable<Boolean> saveImageFromBitmapAndShare(final PInfo pInfo,
                                                           final ImageResponse image,
                                                           @From final int from) {
        return dataService.createImageFromBitmap(image)
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return dataService.createImage(image.url, image.sharingUrl, image.imageType);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return dataService.createVideo(image.videoUrl);
                    }
                })
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        share(pInfo, image, from);
                        return true;
                    }
                });
    }


    private void share(final PInfo pInfo, final ImageResponse image, @From final int from) {
        final Activity activity = getAttachedObject();
        if (activity == null) return;

        sendLocaliticsSharePlaceEvent(pInfo.getPackageName(), from);

        final Context context = activity.getApplicationContext();
        MessengerConfigs currentMessengerConfigs = null;
        sendActionShare(from, image, pInfo);
        for (MessengerConfigs messengerConfigs : config.messengerConfigs()) {
            if (messengerConfigs.applicationId.equals(pInfo.getPackageName())) {
                currentMessengerConfigs = messengerConfigs;
                break;
            }
        }
        String type = "image/*";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setPackage(pInfo.getPackageName());
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        final String fullFileName = FileService.getFullFileName(context,
                image.url, image.imageType, tokenStorage.isCreateAlbum(), false);
        File media = new File(fullFileName);
        if (currentMessengerConfigs != null) {
            if (config.sharingInformationEnabled()) {
                if (currentMessengerConfigs.supportsImageTextReply) {
                    if (image.isGIF && !currentMessengerConfigs.supportsGIF) {
                        type = "video/*";
                        final String fileName = FileService.getFullFileName(context,
                                image.videoUrl, "", tokenStorage.isCreateAlbum(), true);
                        Uri uri = Uri.fromFile(new File(fileName));
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                    } else {
                        Uri uri = Uri.fromFile(media);
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                    }

                    if (config != null && config.replyUrl() != null && config.replyUrlText() != null) {
                        share.putExtra(Intent.EXTRA_TEXT, config.replyUrl()
                                + Strings.ENTER + config.replyUrlText());
                    }
                } else if (currentMessengerConfigs.supportsImageReply) {
                    if (image.isGIF && !currentMessengerConfigs.supportsGIF) {
                        type = "video/*";
                        final String fileName = FileService.getFullFileName(context,
                                image.videoUrl, "", tokenStorage.isCreateAlbum(), true);
                        Uri uri = Uri.fromFile(new File(fileName));
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                    } else {
                        Uri uri = Uri.fromFile(media);
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                    }
                } else {
                    type = "text/plain";
                    share.putExtra(Intent.EXTRA_TEXT, image.sharingUrl);
                }
            } else {
                if (currentMessengerConfigs.supportsImageReply) {
                    Uri uri = Uri.fromFile(media);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                } else {
                    type = "text/plain";
                    share.putExtra(Intent.EXTRA_TEXT, image.url);
                }
            }
        } else {
            Uri uri = Uri.fromFile(media);
            share.putExtra(Intent.EXTRA_STREAM, uri);
        }
        share.setType(type);

        // TODO (a.m.) send share event to localitics;

        try {
            activity.startActivity(share);
        } catch (ActivityNotFoundException ex) {
            toastPresenter.show(R.string.error_application_not_content_support);
        }
    }

    public Observable<Boolean> vkGetDialogs(final VKRequest.VKRequestListener vkRequestListener) {
        return Observable.create(new RequestFunction<Boolean>() {
            @Override
            protected Boolean request() {
                VKRequest dialogsRequest = new VKRequest("messages.getDialogs",
                        VKParameters.from(VKApiConst.COUNT, "3"),
                        VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
                dialogsRequest.executeWithListener(vkRequestListener);
                return true;
            }
        });
    }

    public Observable<Boolean> shareToVk(final ImageResponse image, final VKApiUser user,
                                         final VKRequest.VKRequestListener vkRequestListener, int from) {

        sendLocaliticsSharePlaceEvent(PackageManagerTools.Messanger.VKONTAKTE.getPackagename(), from);

        return dataService.createImageFromBitmap(image)
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return dataService.createImage(image.url, image.sharingUrl, image.imageType);
                    }
                }).map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                                image.url, image.imageType, tokenStorage.isCreateAlbum(), false));
//                        if (image.isGIF){
//                            VKUploadDocRequest vkUploadDocRequest = new VKUploadDocRequest(media);
//                            vkUploadDocRequest.executeWithListener(new VKRequest.VKRequestListener() {
//                                @Override
//                                public void onComplete(VKResponse response) {
//                                    super.onComplete(response);
//                                }
//
//                                @Override
//                                public void onError(VKError error) {
//                                    super.onError(error);
//                                }
//                            });
//                        }else {
                        VKUploadMessagesPhotoRequest serverRequest = new VKUploadMessagesPhotoRequest(media);
                        serverRequest.executeWithListener(new VKUploadMessagesPhotoRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                final VKPhotoArray arrayPhoto = (VKPhotoArray) response.parsedModel;
                                VKApiPhoto vkApiPhoto = arrayPhoto.get(0);
                                String attachString = "photo" + vkApiPhoto.owner_id + "_" + vkApiPhoto.id;
                                VKRequest sendRequest = new VKRequest("messages.send",
                                        VKParameters.from(VKApiConst.USER_ID, user.id, VKApiConst.MESSAGE,
                                                config.replyUrl() != null ? config.replyUrl() : "",
                                                "attachment", attachString),
                                        VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
                                sendRequest.executeWithListener(vkRequestListener);
                            }
                        });
//                        }
                        return true;
                    }
                });
    }

    public Observable<Boolean> shareToFb(final ImageResponse image, int from) {

        sendLocaliticsSharePlaceEvent(PackageManagerTools.Messanger.FACEBOOK_MESSANGER.getPackagename(), from);

        return dataService.createImageFromBitmap(image)
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return dataService.createImage(image.url, image.sharingUrl, image.imageType);
                    }
                }).map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        File media = new File(FileService.getFullFileName(getAttachedObject(),
                                image.url, image.imageType, tokenStorage.isCreateAlbum(), false));
                        String mimeType = "image/*";

                        ShareToMessengerParams shareToMessengerParams =
                                ShareToMessengerParams.newBuilder(Uri.fromFile(media), mimeType)
                                        .build();
                        MessengerUtils.shareToMessenger(getAttachedObject(), 12347,
                                shareToMessengerParams);
                        return true;
                    }
                });
    }

    public void shareVK(final ImageResponse imageResponse, final VKApiUser user,
                        final VKRequest.VKRequestListener vkRequestListener) {
        VKRequest sendRequest = new VKRequest("messages.send",
                VKParameters.from(VKApiConst.USER_ID, user.id, VKApiConst.MESSAGE,
                        config.replyUrl() + Strings.ENTER + imageResponse.sharingUrl),
                VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
        sendRequest.executeWithListener(vkRequestListener);
    }

    public void shareWithChooser(final ImageResponse image, @From final int from) {
        chooseDialogBuilder.setCallback(new ChooseDialogBuilder.ChooseDialogCallBack() {
            @Override
            public void share(PInfo pInfo, ImageResponse imageResponse) {
                localyticsController.shareOutside(pInfo.getApplicationName());
                saveImageFromBitmapAndShare(pInfo, imageResponse, from)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });
        chooseDialogBuilder.openDialog(packages, image);
    }

    private void sendActionShare(@From int from, ImageResponse image, PInfo pInfo) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        ArrayList<Action> actions = new ArrayList<>();
        switch (from) {
            case PERSONAL:
                actions.add(Action.getShareActionForPersonal(image.id, Timestamp.getUTC(), pInfo.getPackageName()));
                break;
            case GOLD_FAVORITES:
                actions.add(Action.getShareActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId, pInfo.getPackageName()));
                break;
            case GOLD_NOVELTY:
                actions.add(Action.getShareActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId, pInfo.getPackageName()));
                break;
            default:
                break;
        }
        dataService.postShare(new ShareRequest(actions)).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).
                subscribe();

    }

    public void sendActionHide(@From int from, ImageResponse image) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        ArrayList<Action> actions = new ArrayList<>();
        switch (from) {
            case PERSONAL:
                actions.add(Action.getLikeDislikeHideActionForPersonal(image.id,
                        Timestamp.getUTC()));
                break;
            case CATEGORY_FEED:
                actions.add(Action.getLikeDislikeHideAction(image.id,
                        Timestamp.getUTC(), image.categoryId));
                break;
            case GOLD_FAVORITES:
                actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(image.id,
                        Timestamp.getUTC(), image.categoryId));
                break;
            default:
            case MAIN_FEED:
                actions.add(Action.getLikeDislikeHideActionForMainFeed(image.id, Timestamp.getUTC()));
                break;
        }
        dataService.hide(new HideRequest(actions))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void sendActionLikeDislike(@From int from, ImageResponse image) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        localyticsController.like(image.isGIF ? LocalyticsController.GIF : LocalyticsController.JPEG);

        final ArrayList<Action> actions = new ArrayList<>();
        switch (from) {
            case PERSONAL:
                actions.add(Action.getLikeDislikeHideActionForPersonal(image.id, Timestamp.getUTC()));
                break;
            case SharingService.GOLD_FAVORITES:
                actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId));
                break;
            case SharingService.GOLD_NOVELTY:
                actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId));
                break;
            default:
                break;
        }
        if (image.liked) {
            subscriptions.add(dataService.deleteImage(image)
                    .flatMap(new Func1<Boolean, Observable<String>>() {
                        @Override
                        public Observable<String> call(Boolean aBoolean) {
                            return dataService.dislike(new DislikeRequest(actions));
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        } else {
            subscriptions.add(dataService.createImage(image.url, image.sharingUrl, image.imageType)
                    .flatMap(new Func1<Boolean, Observable<String>>() {
                        @Override
                        public Observable<String> call(Boolean aBoolean) {
                            return dataService.like(new LikeRequest(actions));
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }
    }

    private void sendLocaliticsSharePlaceEvent(String packagename, @From int from) {
        String applicationName = null;
        /*
            Find Application name by packagename among famous applications to PackageManagerTools.Messanger;
          */
        for (PackageManagerTools.Messanger messanger : PackageManagerTools.Messanger.values()) {
            if (messanger.getPackagename().equals(packagename)) {
                applicationName = messanger.getAppname();
                break;
            }
        }
        /*
            If unable to find application name among famous applications to PackageManagerTools.Messanger,
            then take application name from application package list on device;
         */
        if (applicationName == null) {
            for (PInfo pInfo : packages) {
                if (pInfo.getPackageName().equals(packagename)) {
                    applicationName = pInfo.getApplicationName();
                    break;
                }
            }
        }

        if (applicationName != null) {
            localyticsController.share(applicationName);
        }
        localyticsController.sendXPics();
        switch (from) {
            case PERSONAL:
                localyticsController.sendSharePlace(LocalyticsController.HISTORY);
                break;
            case SharingService.GOLD_FAVORITES:
                localyticsController.sendSharePlace(LocalyticsController.FAVORITES);
                break;
            case SharingService.GOLD_NOVELTY:
                localyticsController.sendSharePlace(LocalyticsController.NEW);
                break;
            default:
                break;
        }
    }

    private String getFileName(String url, String fileType) {
        return url + Strings.DOT + fileType;
    }

    public void unsubscribe() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }
}
