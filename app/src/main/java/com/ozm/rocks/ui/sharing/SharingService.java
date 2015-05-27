package com.ozm.rocks.ui.sharing;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.ShareRequest;
import com.ozm.rocks.data.api.response.GifMessengerOrder;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.MessengerConfigs;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.Strings;
import com.ozm.rocks.util.Timestamp;

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
public class SharingService {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PERSONAL, MAIN_FEED})
    public @interface From {

    }

    public static final int PERSONAL = 1;
    public static final int MAIN_FEED = 2;
    private DataService dataService;
    private Application application;
    private Config config;
    private final SharingDialogBuilder sharingDialogBuilder;
    private ArrayList<PInfo> packages;
    private SharingDialogHide sharingDialogHide;

    @Nullable
    private CompositeSubscription subscriptions;

    @Inject
    public SharingService(DataService dataService, Application application,
                          SharingDialogBuilder sharingDialogBuilder) {
        this.dataService = dataService;
        this.application = application;
        this.sharingDialogBuilder = sharingDialogBuilder;
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

    public void setHideCallback(SharingDialogHide sharingDialogHide) {
        this.sharingDialogHide = sharingDialogHide;
    }

    public void showSharingDialog(final ImageResponse image, @From final int from) {
        if (subscriptions == null) {
            return;
        } else if (subscriptions.isUnsubscribed()) {
            subscriptions = new CompositeSubscription();
        }
        subscriptions.add(dataService.getConfig().
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
                                        saveImageAndShare(pInfo, image, from);
                                    }

                                    @Override
                                    public void hideImage(ImageResponse imageResponse) {
                                        if (sharingDialogHide != null) {
                                            sharingDialogHide.hide();
                                        }
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
        subscriptions.add(dataService.createImage(image.url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean aBoolean) {
                                        MessengerConfigs currentMessengerConfigs = null;
                                        sendAction(from, image, pInfo);
                                        for (MessengerConfigs messengerConfigs : config.messengerConfigs()) {
                                            for (PInfo pInfo : packages) {
                                                if (messengerConfigs.applicationId.equals(pInfo.getPackageName())) {
                                                    currentMessengerConfigs = messengerConfigs;
                                                    break;
                                                }
                                            }
                                            if (currentMessengerConfigs != null) {
                                                break;
                                            }
                                        }
                                        String type = "text/plain";
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType(type);
                                        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        share.setPackage(pInfo.getPackageName());
                                        if (currentMessengerConfigs != null) {
                                            if (currentMessengerConfigs.supportsImageTextReply) {
                                                share.putExtra(Intent.EXTRA_TEXT, image.url + Strings.ENTER
                                                        + config.replyUrl() + Strings.ENTER
                                                        + config.replyUrlText());
                                            } else if (currentMessengerConfigs.supportsImageReply) {
                                                share.putExtra(Intent.EXTRA_TEXT, image.sharingUrl);
                                            }
                                        }
                                        application.startActivity(share);
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Timber.w(throwable, "Save image");
                                    }
                                }
                        )
        );
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
                + config.replyUrl() + Strings.ENTER
                + config.replyUrlText());
        Intent chooser = Intent.createChooser(share, "Share to");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(chooser);
    }

    private void sendAction(@From int from, ImageResponse image, PInfo pInfo) {
        ArrayList<Action> actions = new ArrayList<>();
        switch (from) {
            case PERSONAL:
                actions.add(Action.getShareActionForMainFeed(image.id, Timestamp.getUTC(), pInfo.getPackageName()));
                break;
            default:
            case MAIN_FEED:
                actions.add(Action.getShareActionForMainFeed(image.id, Timestamp.getUTC(), pInfo.getPackageName()));
                break;
        }
        dataService.postShare(new ShareRequest(actions)).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).
                subscribe();

    }

    public void unsubscribe() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

    public interface SharingDialogHide {
        void hide();
    }
}
