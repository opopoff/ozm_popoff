package com.ozm.rocks.ui.sharing;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.response.GifMessengerOrder;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.MessengerConfigs;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;
import com.ozm.rocks.util.Strings;
import com.ozm.rocks.util.Timestamp;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Danil on 22.05.2015.
 */
@ApplicationScope
public class SharingService {
    private DataService dataService;
    private Application application;
    private Config config;
    private final SharingDialogBuilder sharingDialogBuilder;
    private ArrayList<PInfo> packages;
    private PackageManagerTools packageManagerTools;

    @Nullable
    private CompositeSubscription subscriptions;

    @Inject
    public SharingService(DataService dataService, Application application,
                          SharingDialogBuilder sharingDialogBuilder, PackageManagerTools packageManagerTools) {
        this.dataService = dataService;
        this.application = application;
        this.packageManagerTools = packageManagerTools;
        this.sharingDialogBuilder = sharingDialogBuilder;
        subscriptions = new CompositeSubscription();
    }

    public void sendPackages() {
        subscriptions = new CompositeSubscription();
        packages = packageManagerTools.getInstalledPackages();
        subscriptions.add(dataService.sendPackages(packages).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).
                subscribe());
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
                                SharingService.this.config = config;
                                ArrayList<PInfo> pInfos = new ArrayList<>();
                                if (image.isGIF) {
                                    for (GifMessengerOrder gifMessengerOrder : config.gifMessengerOrders()) {
                                        for (PInfo pInfo : packages) {
                                            if (gifMessengerOrder.applicationId.equals(pInfo.getPname())) {
                                                pInfos.add(pInfo);
                                            }
                                        }
                                    }
                                } else {
                                    for (MessengerOrder messengerOrder : config.messengerOrders()) {
                                        for (PInfo pInfo : packages) {
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
                                        ArrayList<Action> actions = new ArrayList<>();
                                        actions.add(Action.getLikeDislikeHideActionForMainFeed(
                                                image.id, Timestamp.getUTC()));
//                                        hide(new HideRequest(actions));
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
                        for (MessengerConfigs messengerConfigs : config.messengerConfigs()) {
                            for (PInfo pInfo : packages) {
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
                                        + config.replyUrl() + Strings.ENTER
                                        + config.replyUrlText());
                            } else if (currentMessengerConfigs.supportsImageReply) {
                                share.putExtra(Intent.EXTRA_TEXT, image.sharingUrl);
                            }
                        }
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

    public void unsubscribe(){
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }
}
