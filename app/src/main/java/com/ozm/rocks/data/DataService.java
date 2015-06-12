package com.ozm.rocks.data;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.ozm.rocks.data.api.OzomeApiService;
import com.ozm.rocks.data.api.RegistrationApiService;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.request.RequestDeviceId;
import com.ozm.rocks.data.api.request.ShareRequest;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.Messenger;
import com.ozm.rocks.data.api.response.PackageRequest;
import com.ozm.rocks.data.api.response.RestConfig;
import com.ozm.rocks.data.api.response.RestRegistration;
import com.ozm.rocks.data.rx.RequestFunction;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.ui.message.NoInternetPresenter;
import com.ozm.rocks.util.DeviceManagerTools;
import com.ozm.rocks.util.Encoding;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;
import com.ozm.rocks.util.Strings;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

@ApplicationScope
public class DataService {
    public static final String NO_INTERNET_CONNECTION = "No internet connection";

    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final OzomeApiService ozomeApiService;
    private final RegistrationApiService registrationApiService;
    private final NoInternetPresenter noInternetPresenter;
    private final FileService fileService;
    private final PackageManagerTools packageManagerTools;
    private final Clock clock;

    @Nullable
    private ReplaySubject<ArrayList<PInfo>> packagesReplaySubject;
    @Nullable
    private ReplaySubject<Config> configReplaySubject;

    @Inject
    public DataService(Application application, OzomeApiService ozomeApiService,
                       FileService fileService, PackageManagerTools packageManagerTools,
                       NoInternetPresenter noInternetPresenter, RegistrationApiService registrationApiService,
                       Clock clock) {
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = application;
        this.ozomeApiService = ozomeApiService;
        this.fileService = fileService;
        this.noInternetPresenter = noInternetPresenter;
        this.packageManagerTools = packageManagerTools;
        this.registrationApiService = registrationApiService;
        this.clock = clock;
    }

    public Observable<List<ImageResponse>> getGeneralFeed(int from, int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.getGeneralFeed(from, to);
    }

    public Observable<List<ImageResponse>> generalFeedUpdate(final int from, final int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.generalFeedUpdate().flatMap(new Func1<String, Observable<List<ImageResponse>>>() {
            @Override
            public Observable<List<ImageResponse>> call(String response) {
                if (response.equals("success")) {
                    return getGeneralFeed(from, to);
                } else {
                    return Observable.empty();
                }
            }
        });
    }

    public Observable<List<ImageResponse>> categoryFeedUpdate(final long categoryId, final int from, final int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.categoryFeedUpdate(categoryId).
                flatMap(new Func1<String, Observable<List<ImageResponse>>>() {
                    @Override
                    public Observable<List<ImageResponse>> call(String response) {
                        if (response.equals("success")) {
                            return getCategoryFeed(categoryId, from, to);
                        } else {
                            return Observable.empty();
                        }
                    }
                });
    }

    public Observable<List<ImageResponse>> getCategoryFeed(final long categoryId, final int from, final int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.getCategoryFeed(categoryId, from, to);
    }

    public Observable<List<ImageResponse>> getMyCollection() {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.getMyCollection();
    }

    public Observable<String> like(LikeRequest likeRequest) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.postLike(likeRequest);
    }

    public Observable<String> dislike(DislikeRequest dislikeRequest) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.postDislike(dislikeRequest);
    }

    public Observable<String> hide(HideRequest hideRequest) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.postHide(hideRequest);
    }

    public Observable<String> postShare(ShareRequest shareRequest) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.postShare(shareRequest);
    }

    private boolean hasInternet() {
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Observable<Config> getConfig() {
//        if (!hasInternet()) {
//            noInternetPresenter.showMessageWithTimer();
//            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
//        }
        if (configReplaySubject != null) {
            return configReplaySubject;
        }
        configReplaySubject = ReplaySubject.create();
        ozomeApiService.getConfig().
                map(new Func1<RestConfig, Config>() {
                    @Override
                    public Config call(RestConfig restConfig) {
                        return Config.from(restConfig);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configReplaySubject);

        return configReplaySubject;
    }

    public Observable<Boolean> createImage(final String url, final String sharingUrl) {
        return Observable.create(new RequestFunction<Boolean>() {
            @Override
            protected Boolean request() {
                return fileService.createFile(url, false);
            }
        }).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                return fileService.createFile(sharingUrl, true);
            }
        });
    }

    public Observable<Boolean> deleteImage(final String url) {
        return Observable.create(new RequestFunction<Boolean>() {
            @Override
            protected Boolean request() {
                return fileService.deleteFile(url);
            }
        });
    }

    public Observable<retrofit.client.Response> sendPackages(ArrayList<PInfo> pInfos) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        noInternetPresenter.hideMessage();
        List<Messenger> messengers = new ArrayList<>();
        for (PInfo pInfo : pInfos) {
            messengers.add(Messenger.create(pInfo.getPackageName()));
        }
        return ozomeApiService.sendPackages(PackageRequest.create(messengers));
    }

    public Observable<ArrayList<PInfo>> getPackages() {
        if (packagesReplaySubject != null) {
            return packagesReplaySubject;
        }
        packagesReplaySubject = ReplaySubject.create();
        Observable.create(new RequestFunction<ArrayList<PInfo>>() {
            @Override
            protected ArrayList<PInfo> request() {
                return packageManagerTools.getInstalledPackages();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(packagesReplaySubject);

        return packagesReplaySubject;
    }

    public Observable<CategoryResponse> getCategories() {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.getCategories();
    }

    public Observable<List<ImageResponse>> getGoldFeed(final long categoryId, final int from, final int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return ozomeApiService.getGoldFeed(categoryId, from, to);
    }

    public Observable<RestRegistration> register() {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }

        final String uniqueDeviceId = DeviceManagerTools.getUniqueDeviceId(context);
        RequestDeviceId requestDeviceId = new RequestDeviceId(uniqueDeviceId);
        Gson gson = new Gson();
        String deviceIdJson = gson.toJson(requestDeviceId);

        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(RegistrationApiService.URL_REGISTRATION);
        signatureBuilder.append(deviceIdJson);
        signatureBuilder.append(RegistrationApiService.USER_KEY);
        signatureBuilder.append(RegistrationApiService.USER_SECRET);
        signatureBuilder.append(clock.unixTime());
        String signature = Encoding.base64HmacSha256(signatureBuilder.toString());

        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(RegistrationApiService.USER_KEY);
        headerBuilder.append(Strings.GUP);
        headerBuilder.append(clock.unixTime());
        headerBuilder.append(Strings.GUP);
        headerBuilder.append(signature);

        return registrationApiService.register(headerBuilder.toString(), requestDeviceId);
    }
}
