package com.umad.wat.data;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.google.gson.Gson;
import com.umad.BuildConfig;
import com.umad.R;
import com.umad.wat.ApplicationScope;
import com.umad.wat.base.ActivityConnector;
import com.umad.wat.base.tools.ToastPresenter;
import com.umad.wat.data.analytics.LocalyticsController;
import com.umad.wat.data.api.model.Config;
import com.umad.wat.data.api.request.Action;
import com.umad.wat.data.api.request.DislikeRequest;
import com.umad.wat.data.api.request.HideRequest;
import com.umad.wat.data.api.request.LikeRequest;
import com.umad.wat.data.api.request.ShareRequest;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.api.response.MessengerConfigs;
import com.umad.wat.data.model.PInfo;
import com.umad.wat.data.rx.RequestFunction;
import com.umad.wat.ui.screen.main.SendFriendDialogBuilder;
import com.umad.wat.ui.screen.sharing.choose.dialog.ChooseDialogBuilder;
import com.umad.wat.util.PackageManagerTools;
import com.umad.wat.util.Timestamp;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKDocsArray;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.photo.VKUploadMessagesPhotoRequest;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@ApplicationScope
public class SharingService extends ActivityConnector<Activity> {
    public static final int PERSONAL = 1;
    public static final int GOLD_FAVORITES = 2;
    public static final int GOLD_RANDOM = 3;
    public static final int GENERAL = 4;
    private static final List<String> vkMessengers = Arrays.asList("com.vkontakte.android");
    private final DataService dataService;
    private final LocalyticsController localyticsController;
    private final ChooseDialogBuilder chooseDialogBuilder;
    private final TokenStorage tokenStorage;
    private final ToastPresenter toastPresenter;
    private final SendFriendDialogBuilder sendFriendDialogBuilder;
    @Nullable
    private CompositeSubscription subscriptions;

    @Inject
    public SharingService(DataService dataService,
                          ChooseDialogBuilder chooseDialogBuilder,
                          LocalyticsController localyticsController,
                          ToastPresenter toastPresenter,
                          TokenStorage tokenStorage,
                          SendFriendDialogBuilder sendFriendDialogBuilder) {
        this.dataService = dataService;
        this.chooseDialogBuilder = chooseDialogBuilder;
        this.localyticsController = localyticsController;
        this.tokenStorage = tokenStorage;
        this.toastPresenter = toastPresenter;
        this.sendFriendDialogBuilder = sendFriendDialogBuilder;
        subscriptions = new CompositeSubscription();
    }

    public Observable<Boolean> saveImageFromCacheAndShare(final PInfo pInfo,
                                                          final ImageResponse image,
                                                          @From final int from) {
        sendActionShare(from, image);
        return saveImageFromCache(pInfo, image, from)
                .map(new Func1<TypeAndUri, Boolean>() {
                    @Override
                    public Boolean call(TypeAndUri typeAndUri) {
                        sendLocaliticsSharePlaceEvent(pInfo.getPackageName(), pInfo.getApplicationName(), from);
                        share(pInfo, typeAndUri.getUri(), typeAndUri.getType(), image.sharingUrl);
                        return true;
                    }
                });
    }

    private void share(final PInfo pInfo, Uri uri, String type, String url) {
        final Activity activity = getAttachedObject();
        if (activity == null) return;
        Intent share = new Intent(Intent.ACTION_SEND);
        if (uri != null) {
            share.putExtra(Intent.EXTRA_STREAM, uri);
        } else {
            share.putExtra(Intent.EXTRA_TEXT, url);
        }
        share.setPackage(pInfo.getPackageName());
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        share.setType(type);

        try {
            activity.startActivity(share);
        } catch (ActivityNotFoundException ex) {
            toastPresenter.show(R.string.error_application_not_content_support);
        }
    }

    public Observable<Boolean> shareToVk(final ImageResponse image, final VKApiUser user,
                                         final VKRequest.VKRequestListener vkRequestListener, int from,
                                         final boolean sendLinkToVk) {
        final String packagename = PackageManagerTools.Messanger.VKONTAKTE.getPackagename();
        sendLocaliticsSharePlaceEvent(packagename, null, from);
        sendActionShare(from, image);

        return dataService.createImageFromCache(image, null)
                .flatMap(new Func1<Boolean, Observable<Config>>() {
                    @Override
                    public Observable<Config> call(Boolean aBoolean) {
                        if (!aBoolean) {

                        }
                        return dataService.getConfig();
                    }
                })
                .map(new Func1<Config, Boolean>() {
                    @Override
                    public Boolean call(final Config config) {
                        File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                                image.url, image.imageType, tokenStorage.isCreateAlbum(), false));
                        if (image.isGIF) {
                            VKRequest vkUploadDocRequest = VKApi.docs().uploadDocRequest(media);
                            vkUploadDocRequest.executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    final VKDocsArray docsArray =
                                            (VKDocsArray) response.parsedModel;
                                    VKApiDocument vkApiDocument = docsArray.get(0);
                                    String link = "";
                                    if (sendLinkToVk) {
                                        link = config.replyUrl();
                                    }
                                    VKRequest sendRequest = new VKRequest("messages.send",
                                            VKParameters.from(VKApiConst.USER_ID, user.id,
                                                    VKApiConst.MESSAGE, link,
                                                    "attachment", vkApiDocument.toAttachmentString()),
                                            VKApiDocument.class);
                                    sendRequest.executeWithListener(vkRequestListener);
                                }
                            });
                        } else {
                            VKUploadMessagesPhotoRequest serverRequest = new VKUploadMessagesPhotoRequest(media);
                            serverRequest.executeWithListener(new VKUploadMessagesPhotoRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    if (response != null) {
                                        super.onComplete(response);
                                        final VKPhotoArray arrayPhoto = (VKPhotoArray) response.parsedModel;
                                        VKApiPhoto vkApiPhoto = arrayPhoto.get(0);
                                        String link = "";
                                        if (sendLinkToVk) {
                                            link = config.replyUrl();
                                        }
                                        String attachString = "photo" + vkApiPhoto.owner_id + "_" + vkApiPhoto.id;
                                        VKRequest sendRequest = new VKRequest("messages.send",
                                                VKParameters.from(VKApiConst.USER_ID, user.id,
                                                        VKApiConst.MESSAGE, link,
                                                        "attachment", attachString),
                                                VKApiMessage.class);
                                        sendRequest.executeWithListener(vkRequestListener);
                                    }
                                }
                            });
                        }
                        return true;
                    }
                });
    }

    public Observable<Boolean> shareToFb(final ImageResponse image, final int from) {

        final String packagename = PackageManagerTools.Messanger.FACEBOOK_MESSANGER.getPackagename();
        sendLocaliticsSharePlaceEvent(packagename, null, from);
        sendActionShare(from, image);

        return dataService.createImageFromCache(image, null)
                .map(new Func1<Boolean, Boolean>() {
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

    public void shareWithChooser(final ImageResponse image, @From final int from,
                                 final Action1<Boolean> action) {
        dataService.getPackages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<PInfo>>() {
                    @Override
                    public void call(ArrayList<PInfo> pInfos) {
                        chooseDialogBuilder.setCallback(new ChooseDialogBuilder.ChooseDialogCallBack() {
                            @Override
                            public void share(PInfo pInfo, ImageResponse imageResponse) {
                                localyticsController.shareOutside(pInfo.getApplicationName());
                                saveImageFromCacheAndShare(pInfo, imageResponse, from)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Boolean>() {
                                            @Override
                                            public void call(Boolean aBoolean) {
                                                if (action != null) {
                                                    action.call(true);
                                                }
                                            }
                                        });
                            }
                        });
                        chooseDialogBuilder.openDialog(pInfos, image);
                    }
                });
    }

    public Observable<Boolean> shareWithStandardChooser(final ImageResponse imageResponse,
                                                        @From final int from) {
        return saveImageFromCache(null, imageResponse, from).map(new Func1<TypeAndUri, Boolean>() {
            @Override
            public Boolean call(TypeAndUri typeAndUri) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType(typeAndUri.getType());
                sendIntent.putExtra(Intent.EXTRA_STREAM, typeAndUri.getUri());
                Intent chooserIntent = Intent.createChooser(sendIntent, "");
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                if (getAttachedObject() != null) {
                    getAttachedObject().startActivity(chooserIntent);
                }
                return true;
            }
        });
    }

    public Observable<TypeAndUri> saveImageFromCache(final PInfo pInfo, final ImageResponse image, final int from) {
        return dataService.getConfig().flatMap(new Func1<Config, Observable<TypeAndUri>>() {
            @Override
            public Observable<TypeAndUri> call(Config config) {
                Timber.d("NewConfig: SharingService: success from %s", config.from());
                //
                config.upMessenger(pInfo.getPackageName(), image.isGIF);
                tokenStorage.setConfigString(new Gson().toJson(config.toRest()));
                //
                MessengerConfigs currentMessengerConfigs = null;
                for (MessengerConfigs messengerConfigs : config.messengerConfigs()) {
                    if (messengerConfigs.applicationId.equals(pInfo.getPackageName())) {
                        currentMessengerConfigs = messengerConfigs;
                        break;
                    }
                }
                final MessengerConfigs finalCurrentMessengerConfigs = currentMessengerConfigs;
                // TODO change flatMap on map rx-operator;
                return dataService.createImageFromCache(image, currentMessengerConfigs)
                        .flatMap(new Func1<Boolean, Observable<TypeAndUri>>() {
                            @Override
                            public Observable<TypeAndUri> call(Boolean aBoolean) {
                                return Observable.create(new RequestFunction<TypeAndUri>() {
                                    @Override
                                    protected TypeAndUri request() {
                                        return new TypeAndUri(image, finalCurrentMessengerConfigs,
                                                getAttachedObject(), tokenStorage, from);
                                    }
                                });
                            }
                        });
            }
        });
    }

    private void sendActionShare(@From int from, ImageResponse image) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        ArrayList<Action> actions = new ArrayList<>();
        String packageName = BuildConfig.APPLICATION_ID;
        switch (from) {
            case PERSONAL:
                actions.add(Action.getShareActionForPersonal(image.id, Timestamp.getUTC(), packageName));
                break;
            case GOLD_FAVORITES:
                actions.add(Action.getShareActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId, packageName));
                break;
            case GOLD_RANDOM:
                actions.add(Action.getShareActionForGoldenRandom(image.id, Timestamp.getUTC(),
                        image.categoryId, packageName));
                break;
            case GENERAL:
                actions.add(Action.getShareActionForMainFeed(image.id, Timestamp.getUTC(),
                        packageName));
                break;
            default:
                break;
        }
        dataService.postShare(new ShareRequest(actions)).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).
                subscribe(
                        new Action1<String>() {
                            @Override
                            public void call(String s) {
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (getAttachedObject() != null) {
                                    Toast.makeText(getAttachedObject(), R.string.sharing_service_error_message,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
    }

    public void showSendFriendsDialog() {
        // Костыль, потому, что зачастую у sendFriendDialogBuilder.getAttachedObject = null
        final long interval = 1500;
        Observable.timer(interval, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        sendFriendDialogBuilder.openDialog();
                        localyticsController.setSplashscreenShow();
                        sendFriendDialogBuilder.setCallback(new SendFriendDialogBuilder.ChooseDialogCallBack() {
                            @Override
                            public void share() {
                                sendFriends()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();
                                localyticsController.setShareOzm(LocalyticsController.SPLASHSCREEN);
                            }
                        });
                    }
                });
    }

    public Observable<Boolean> sendFriends() {
        return dataService.getConfig().map(new Func1<Config, Boolean>() {
            @Override
            public Boolean call(Config config) {
                Timber.d("NewConfig: SharingService: success from %s", config.from());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, config.replyUrl());
                sendIntent.setType("text/plain");
                Intent chooserIntent = Intent.createChooser(sendIntent, "");
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                if (getAttachedObject() != null) {
                    getAttachedObject().startActivity(chooserIntent);
                }
                return true;
            }
        });
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
            case GOLD_FAVORITES:
                actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(image.id,
                        Timestamp.getUTC(), image.categoryId));
                break;
            case GOLD_RANDOM:
                actions.add(Action.getLikeDislikeHideActionForGoldenRandom(image.id,
                        Timestamp.getUTC(), image.categoryId));
                break;
            default:
                break;
        }
        dataService.hide(new HideRequest(actions))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<String>() {
                            @Override
                            public void call(String s) {
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.w(throwable.getMessage());
                                toastPresenter.show(R.string.sharing_service_error_message, Toast.LENGTH_LONG);
                            }
                        });
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
            case SharingService.GOLD_RANDOM:
                actions.add(Action.getLikeDislikeHideActionForGoldenRandom(image.id, Timestamp.getUTC(),
                        image.categoryId));
                break;
            default:
                break;
        }
        Observable<String> stringObservable;
        if (image.liked) {
            stringObservable = dataService.deleteImage(image)
                    .flatMap(new Func1<Boolean, Observable<String>>() {
                        @Override
                        public Observable<String> call(Boolean aBoolean) {
                            return dataService.dislike(new DislikeRequest(actions));
                        }
                    });
        } else {
            stringObservable = dataService.createImage(image.url, image.sharingUrl, image.imageType)
                    .flatMap(new Func1<Boolean, Observable<String>>() {
                        @Override
                        public Observable<String> call(Boolean aBoolean) {
                            return dataService.like(new LikeRequest(actions));
                        }
                    });
        }
        Subscription subscribe;
        subscribe = stringObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<String>() {
                            @Override
                            public void call(String s) {
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.w(throwable.getMessage());
                                toastPresenter.show(R.string.sharing_service_error_message, Toast.LENGTH_LONG);
                            }
                        });
        subscriptions.add(subscribe);
    }

    private void sendLocaliticsSharePlaceEvent(String packagename, String appName, @From int from) {
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
        if (applicationName == null && appName != null) {
            applicationName = appName;
        }

        if (applicationName != null) {
            // TODO remove sending of package name on Localitics;
            localyticsController.share(applicationName);
        }
        switch (from) {
            case PERSONAL:
                localyticsController.sendSharePlace(LocalyticsController.HISTORY);
                break;
            case GOLD_FAVORITES:
                localyticsController.sendSharePlace(LocalyticsController.FAVORITES);
                break;
            case GOLD_RANDOM:
                localyticsController.sendSharePlace(LocalyticsController.NEW);
                break;
            default:
                break;
        }
    }

    public void unsubscribe() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PERSONAL, GOLD_FAVORITES, GOLD_RANDOM, GENERAL})
    public @interface From {
    }

    private static class TypeAndUri {
        private String type;
        private Uri uri;

        public TypeAndUri(ImageResponse image, MessengerConfigs currentMessengerConfigs, final Activity activity,
                          TokenStorage tokenStorage, int from) {
            final String fullFileName;
            if (currentMessengerConfigs != null) {
                //for support sharing gif to vk applications
                if (from == GENERAL) {
                    type = "text/plain";
                    uri = null;
                } else if (image.isGIF && vkMessengers.indexOf(currentMessengerConfigs.applicationId) != -1) {
                    type = "*/*";
                    fullFileName = FileService.getFullFileName(activity,
                            image.url, image.imageType, tokenStorage.isCreateAlbum(), false);
                    uri = Uri.fromFile(new File(fullFileName));
                } else if (currentMessengerConfigs.supportsImageTextReply
                        || currentMessengerConfigs.supportsImageReply) {
                    if (image.isGIF && !currentMessengerConfigs.supportsGIF
                            && !currentMessengerConfigs.supportsVideo) {
                        type = "text/plain";
                        uri = null;
                    } else if (image.isGIF && !currentMessengerConfigs.supportsGIF) {
                        type = "video/*";
                        fullFileName = FileService.getFullFileName(activity,
                                image.videoUrl, "", tokenStorage.isCreateAlbum(), true);
                        uri = Uri.fromFile(new File(fullFileName));
                    } else {
                        type = "image/*";
                        fullFileName = FileService.getFullFileName(activity,
                                image.url, image.imageType, tokenStorage.isCreateAlbum(), false);
                        uri = Uri.fromFile(new File(fullFileName));
                    }
                } else {
                    type = "text/plain";
                    uri = null;
                }
            } else {
                if (image.isGIF) {
                    type = "image/gif";
                } else {
                    type = "image/*";
                }
                fullFileName = FileService.getFullFileName(activity,
                        image.url, image.imageType, tokenStorage.isCreateAlbum(), false);
                uri = Uri.fromFile(new File(fullFileName));
            }
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }
    }
}
