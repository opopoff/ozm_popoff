package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.ozm.rocks.base.ActivityConnector;
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
import com.ozm.rocks.data.rx.RequestFunction;
import com.ozm.rocks.data.social.SocialPresenter;
import com.ozm.rocks.data.social.dialog.ApiVkDialogResponse;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.PInfo;
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
    @IntDef({PERSONAL, MAIN_FEED, CATEGORY_FEED, GOLD_CATEGORY_FEED})
    public @interface From {

    }

    public static final int PERSONAL = 1;
    public static final int MAIN_FEED = 2;
    public static final int CATEGORY_FEED = 3;
    public static final int GOLD_CATEGORY_FEED = 4;
    private final DataService dataService;
    private Config config;
    private final LocalyticsController localyticsController;
    private final SharingDialogBuilder sharingDialogBuilder;
    private final ChooseDialogBuilder chooseDialogBuilder;
    private ArrayList<PInfo> packages;
    private final Picasso picasso;
    private final SocialPresenter socialPresenter;
    private final TokenStorage tokenStorage;

    @Nullable
    private CompositeSubscription subscriptions;

    @Inject
    public SharingService(DataService dataService,
                          SharingDialogBuilder sharingDialogBuilder,
                          ChooseDialogBuilder chooseDialogBuilder,
                          LocalyticsController localyticsController, Picasso picasso,
                          SocialPresenter socialPresenter, TokenStorage tokenStorage) {
        this.dataService = dataService;
        this.sharingDialogBuilder = sharingDialogBuilder;
        this.chooseDialogBuilder = chooseDialogBuilder;
        this.localyticsController = localyticsController;
        this.picasso = picasso;
        this.socialPresenter = socialPresenter;
        this.tokenStorage = tokenStorage;
        subscriptions = new CompositeSubscription();
    }

    public void sendPackages(final Action1 action1) {
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
                                return dataService.sendPackages(pInfos);
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

    public void saveImageAndShare(final PInfo pInfo, final ImageResponse image, @From final int from) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        subscriptions.add(dataService.createImage(image.url, image.sharingUrl)
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
                                                           final ImageResponse image, @From final int from) {
        return dataService.createImageFromBitmap(image)
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return dataService.createImage(image.url, image.sharingUrl);
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
        if (currentMessengerConfigs != null) {
            if (config.sharingInformationEnabled()) {
                if (currentMessengerConfigs.supportsImageTextReply) {
                    File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                            image.url, tokenStorage.isCreateAlbum(), false));
                    Uri uri = Uri.fromFile(media);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.putExtra(Intent.EXTRA_TEXT, config.replyUrl()
                            + Strings.ENTER + config.replyUrlText());
                } else if (currentMessengerConfigs.supportsImageReply) {
                    File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                            image.sharingUrl, tokenStorage.isCreateAlbum(), true));
                    Uri uri = Uri.fromFile(media);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                } else {
                    type = "text/plain";
                    share.putExtra(Intent.EXTRA_TEXT, image.sharingUrl);
                }
            } else {
                if (currentMessengerConfigs.supportsImageReply) {
                    File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                            image.url, tokenStorage.isCreateAlbum(), false));
                    Uri uri = Uri.fromFile(media);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                } else {
                    type = "text/plain";
                    share.putExtra(Intent.EXTRA_TEXT, image.url);
                }
            }
        } else {
            File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                    image.url, tokenStorage.isCreateAlbum(), false));
            Uri uri = Uri.fromFile(media);
            share.putExtra(Intent.EXTRA_STREAM, uri);
        }
        share.setType(type);
        getAttachedObject().startActivity(share);
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

    public Observable<Boolean> shareToVk(final ImageResponse imageResponse, final VKApiUser user,
                                         final VKRequest.VKRequestListener vkRequestListener) {
        return dataService.createImageFromBitmap(imageResponse)
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return dataService.createImage(imageResponse.url, imageResponse.sharingUrl);
                    }
                }).map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                                imageResponse.url, tokenStorage.isCreateAlbum(), false));
//                        if (imageResponse.isGIF){
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
                                                config.replyUrl(),
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

    public void shareVK(final ImageResponse imageResponse, final VKApiUser user,
                        final VKRequest.VKRequestListener vkRequestListener) {
        VKRequest sendRequest = new VKRequest("messages.send",
                VKParameters.from(VKApiConst.USER_ID, user.id, VKApiConst.MESSAGE,
                        config.replyUrl() + Strings.ENTER + imageResponse.sharingUrl),
                VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
        sendRequest.executeWithListener(vkRequestListener);
    }

    public Observable<Boolean> shareWithChooser(final ImageResponse image) {
        if (subscriptions == null) {
            return Observable.error(new Exception("SharingService: subscriptions null"));
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        return dataService.createImageFromBitmap(image)
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return dataService.createImage(image.url, image.sharingUrl);
                    }
                }).map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");
                        File media = new File(FileService.getFullFileName(getAttachedObject().getApplicationContext(),
                                image.url, tokenStorage.isCreateAlbum(), false));
                        Uri uri = Uri.fromFile(media);
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        share.putExtra(Intent.EXTRA_TEXT, config.replyUrl()
                                + Strings.ENTER + config.replyUrlText());
                        // Broadcast the Intent.
                        Intent intent = Intent.createChooser(share, "Share to");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        getAttachedObject().startActivity(intent);
                        return true;
                    }
                });
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
                localyticsController.share(LocalyticsController.FAVORITES);
                actions.add(Action.getShareActionForPersonal(image.id, Timestamp.getUTC(), pInfo.getPackageName()));
                break;
            case CATEGORY_FEED:
                localyticsController.share(LocalyticsController.FEED);
                actions.add(Action.getShareAction(image.id, Timestamp.getUTC(), image.categoryId, pInfo
                        .getPackageName()));
                break;
            case GOLD_CATEGORY_FEED:
                localyticsController.share(LocalyticsController.LIBRARY);
                actions.add(Action.getShareActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId, pInfo.getPackageName()));
                break;
            default:
            case MAIN_FEED:
                localyticsController.share(LocalyticsController.FEED);
                actions.add(Action.getShareActionForMainFeed(image.id, Timestamp.getUTC(), pInfo.getPackageName()));
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
            case GOLD_CATEGORY_FEED:
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
                localyticsController.share(LocalyticsController.FAVORITES);
                actions.add(Action.getLikeDislikeHideActionForPersonal(image.id, Timestamp.getUTC()));
                break;
            case SharingService.CATEGORY_FEED:
                localyticsController.share(LocalyticsController.FEED);
                actions.add(Action.getLikeDislikeHideAction(image.id, Timestamp.getUTC(),
                        image.categoryId));
                break;
            case SharingService.GOLD_CATEGORY_FEED:
                localyticsController.share(LocalyticsController.LIBRARY);
                actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(image.id, Timestamp.getUTC(),
                        image.categoryId));
                break;
            default:
            case SharingService.MAIN_FEED:
                localyticsController.share(LocalyticsController.FEED);
                actions.add(Action.getLikeDislikeHideActionForMainFeed(image.id, Timestamp.getUTC()));
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
            subscriptions.add(dataService.createImage(image.url, image.sharingUrl)
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

    public void unsubscribe() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }
}
