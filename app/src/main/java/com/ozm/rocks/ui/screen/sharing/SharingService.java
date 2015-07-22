package com.ozm.rocks.ui.screen.sharing;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

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
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.MessengerConfigs;
import com.ozm.rocks.data.api.response.PackageRequest;
import com.ozm.rocks.data.rx.RequestFunction;
import com.ozm.rocks.data.social.dialog.ApiVkDialogResponse;
import com.ozm.rocks.data.social.docs.ApiVkDocs;
import com.ozm.rocks.data.social.docs.ApiVkDocsResponse;
import com.ozm.rocks.data.social.docs.VKUploadDocRequest;
import com.ozm.rocks.ApplicationScope;
import com.ozm.rocks.ui.screen.main.SendFriendDialogBuilder;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;
import com.ozm.rocks.util.Timestamp;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
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
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

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

    public void sendPackages(final PackageRequest.VkData vkData, final Action1 action1) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }

        subscriptions.add(dataService.getConfigFromPreferences()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                new Action1<Config>() {
                                    @Override
                                    public void call(Config config) {
                                        action1.call(true);
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        reloadConfig(action1, vkData);
                                    }
                                }
                        )
        );
    }

    public void reloadConfig(final Action1 action1, final PackageRequest.VkData vkData) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        subscriptions.add(dataService.getPackages()
                        .flatMap(new Func1<ArrayList<PInfo>, Observable<Response>>() {
                            @Override
                            public Observable<Response> call(ArrayList<PInfo> pInfos) {
                                return dataService.sendPackages(pInfos, vkData);
                            }
                        })
                        .flatMap(new Func1<Response, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Response response) {
                                return dataService.saveConfigToPreferences();
                            }
                        })
                        .flatMap(new Func1<Boolean, Observable<Config>>() {
                            @Override
                            public Observable<Config> call(Boolean b) {
                                return dataService.getConfigFromPreferences();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                new Action1<Config>() {
                                    @Override
                                    public void call(Config config) {
                                        if (action1 != null) {
                                            action1.call(true);
                                        }
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        if (action1 != null) {
                                            action1.call(false);
                                        }
                                    }
                                }
                        )
        );
    }

    public Observable<Boolean> saveImageFromCacheAndShare(final PInfo pInfo,
                                                          final ImageResponse image,
                                                          @From final int from) {
        return dataService.getConfigFromPreferences().flatMap(new Func1<Config, Observable<TypeAndUri>>() {
            @Override
            public Observable<TypeAndUri> call(Config config) {
                MessengerConfigs currentMessengerConfigs = null;
                final String type;
                final Uri uri;
                final String fullFileName;
                for (MessengerConfigs messengerConfigs : config.messengerConfigs()) {
                    if (messengerConfigs.applicationId.equals(pInfo.getPackageName())) {
                        currentMessengerConfigs = messengerConfigs;
                        break;
                    }
                }
                if (currentMessengerConfigs != null) {
                    if (currentMessengerConfigs.supportsImageTextReply
                            || currentMessengerConfigs.supportsImageReply) {
                        if (image.isGIF && !currentMessengerConfigs.supportsGIF) {
                            type = "video/*";
                            fullFileName = FileService.getFullFileName(getAttachedObject(),
                                    image.videoUrl, "", tokenStorage.isCreateAlbum(), true);
                            uri = Uri.fromFile(new File(fullFileName));
                        } else {
                            type = "image/*";
                            fullFileName = FileService.getFullFileName(getAttachedObject(),
                                    image.url, image.imageType, tokenStorage.isCreateAlbum(), false);
                            uri = Uri.fromFile(new File(fullFileName));
                        }
                    } else {
                        type = "text/plain";
                        uri = null;
                    }
                } else {
                    type = "image/*";
                    fullFileName = FileService.getFullFileName(getAttachedObject(),
                            image.url, image.imageType, tokenStorage.isCreateAlbum(), false);
                    uri = Uri.fromFile(new File(fullFileName));
                }
                return dataService.createImageFromCache(image, currentMessengerConfigs)
                        .flatMap(new Func1<Boolean, Observable<TypeAndUri>>() {
                            @Override
                            public Observable<TypeAndUri> call(Boolean aBoolean) {
                                return Observable.create(new RequestFunction<TypeAndUri>() {
                                    @Override
                                    protected TypeAndUri request() {
                                        return new TypeAndUri(type, uri);
                                    }
                                });
                            }
                        });
            }
        }).map(new Func1<TypeAndUri, Boolean>() {
            @Override
            public Boolean call(TypeAndUri typeAndUri) {
                sendLocaliticsSharePlaceEvent(pInfo.getPackageName(), pInfo.getApplicationName(), from);
                sendActionShare(from, image, pInfo.getPackageName());
                share(pInfo, typeAndUri.getUri(), typeAndUri.getType(), image.url);
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
                                         final VKRequest.VKRequestListener vkRequestListener, int from) {
        final String packagename = PackageManagerTools.Messanger.VKONTAKTE.getPackagename();
        sendLocaliticsSharePlaceEvent(packagename, null, from);
        sendActionShare(from, image, packagename);

        return dataService.createImageFromCache(image, null)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                                image.url, image.imageType, tokenStorage.isCreateAlbum(), false));
                        if (image.isGIF) {
                            VKUploadDocRequest vkUploadDocRequest = new VKUploadDocRequest(media);
                            vkUploadDocRequest.executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    if (response != null) {
                                        super.onComplete(response);
                                        final ApiVkDocsResponse apiVkDocsResponse = (ApiVkDocsResponse) response.parsedModel;
                                        ApiVkDocs apiVkDocs = apiVkDocsResponse.items[0];
                                        String attachString = "doc" + apiVkDocs.ownerId + "_" + apiVkDocs.id;
                                        VKRequest sendRequest = new VKRequest("messages.send",
                                                VKParameters.from(VKApiConst.USER_ID, user.id,
                                                        "attachment", attachString),
                                                VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
                                        sendRequest.executeWithListener(vkRequestListener);
                                    }
                                }

                                @Override
                                public void onError(VKError error) {
                                    super.onError(error);
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
                                        String attachString = "photo" + vkApiPhoto.owner_id + "_" + vkApiPhoto.id;
                                        VKRequest sendRequest = new VKRequest("messages.send",
                                                VKParameters.from(VKApiConst.USER_ID, user.id,
                                                        "attachment", attachString),
                                                VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
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
        sendActionShare(from, image, packagename);

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

    public void shareWithChooser(final ImageResponse image, @From final int from) {
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
                                        .subscribe();
                            }
                        });
                        chooseDialogBuilder.openDialog(pInfos, image);
                    }
                });
    }

    private void sendActionShare(@From int from, ImageResponse image, String packageName) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        ArrayList<Action> actions = new ArrayList<>();
        switch (from) {
            case PERSONAL:
                actions.add(Action.getShareActionForPersonal(image.id, Timestamp.getUTC(), packageName));
                break;
            case GOLD_FAVORITES:
                actions.add(Action.getShareActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId, packageName));
                break;
            case GOLD_NOVELTY:
                actions.add(Action.getShareActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId, packageName));
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
                                    Toast.makeText(getAttachedObject(), getAttachedObject()
                                                    .getResources().getString(R.string.sharing_service_error_message),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

    }

    public void showSendFriendsDialog() {
        final long interval = 1500;
        Observable.timer(interval, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        sendFriendDialogBuilder.openDialog();
                        sendFriendDialogBuilder.setCallback(new SendFriendDialogBuilder.ChooseDialogCallBack() {
                            @Override
                            public void share() {
                                sendFriends();
                                localyticsController.setShareOzm(LocalyticsController.SPLASHSCREEN);
                            }
                        });
                    }
                });
    }

    public void sendFriends() {
        dataService.getConfigFromPreferences().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Config>() {
                    @Override
                    public void call(Config config) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, config.replyUrl());
                        sendIntent.setType("text/plain");
                        Intent chooserIntent = Intent.createChooser(sendIntent, "");
                        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        getAttachedObject().startActivity(chooserIntent);
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
                .subscribe(
                        new Action1<String>() {
                            @Override
                            public void call(String s) {
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (getAttachedObject() != null) {
                                    Toast.makeText(getAttachedObject(), getAttachedObject()
                                                    .getResources().getString(R.string.sharing_service_error_message),
                                            Toast.LENGTH_LONG).show();
                                }
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
                    .subscribe(
                            new Action1<String>() {
                                @Override
                                public void call(String s) {
                                }
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (getAttachedObject() != null) {
                                        Toast.makeText(getAttachedObject(), getAttachedObject()
                                                        .getResources().getString(R.string.sharing_service_error_message),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }));
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
                    .subscribe(
                            new Action1<String>() {
                                @Override
                                public void call(String s) {
                                }
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (getAttachedObject() != null) {
                                        Toast.makeText(getAttachedObject(), getAttachedObject()
                                                        .getResources().getString(R.string.sharing_service_error_message),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }));
        }
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
        if (applicationName == null) {
            if (appName != null) {
                applicationName = appName;
            }
        }

        if (applicationName != null) {
            localyticsController.share(applicationName);
        }
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

    public void unsubscribe() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

    private static class TypeAndUri {
        private String type;
        private Uri uri;

        public TypeAndUri(String type, Uri uri) {
            this.type = type;
            this.uri = uri;
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
