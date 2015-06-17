package com.ozm.rocks.data;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.ozm.rocks.data.api.OzomeApiService;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.request.CategoryPinRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final NoInternetPresenter noInternetPresenter;
    private final FileService fileService;
    private final PackageManagerTools packageManagerTools;
    private final TokenStorage tokenStorage;
    private final Clock clock;

    @Nullable
    private ReplaySubject<ArrayList<PInfo>> packagesReplaySubject;
    @Nullable
    private ReplaySubject<Config> configReplaySubject;

    @Inject
    public DataService(Application application, Clock clock, TokenStorage tokenStorage,
                       FileService fileService, PackageManagerTools packageManagerTools,
                       NoInternetPresenter noInternetPresenter, OzomeApiService ozomeApiService) {
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = application;
        this.fileService = fileService;
        this.noInternetPresenter = noInternetPresenter;
        this.packageManagerTools = packageManagerTools;
        this.ozomeApiService = ozomeApiService;
        this.tokenStorage = tokenStorage;
        this.clock = clock;
    }

    public Observable<List<ImageResponse>> getGeneralFeed(int from, int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        Map<String, String> params = new HashMap<>();
        params.put(OzomeApiService.PARAM_FROM, String.valueOf(from));
        params.put(OzomeApiService.PARAM_TO, String.valueOf(to));
        String url = insertUrlParam(OzomeApiService.URL_FEED, params);
        String header = createHeader(
                url,
                Strings.EMPTY,
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.getGeneralFeed(header, from, to);
    }

    public Observable<List<ImageResponse>> generalFeedUpdate(final int from, final int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        String header = createHeader(
                OzomeApiService.URL_FEED_UPDATE,
                Strings.EMPTY,
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.generalFeedUpdate(header).flatMap(
                new Func1<String, Observable<List<ImageResponse>>>() {
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

        final String url = insertUrlPath(OzomeApiService.URL_CATEGORY_FEED_UPDATE, String.valueOf(categoryId));
        String header = createHeader(
                url,
                Strings.EMPTY,
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );

        return ozomeApiService.categoryFeedUpdate(header, categoryId).
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
        String url = insertUrlPath(OzomeApiService.URL_CATEGORY_FEED, String.valueOf(categoryId));
        Map<String, String> params = new HashMap<>();
        params.put(OzomeApiService.PARAM_FROM, String.valueOf(from));
        params.put(OzomeApiService.PARAM_TO, String.valueOf(to));
        url = insertUrlParam(url, params);
        String header = createHeader(
                url,
                Strings.EMPTY,
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.getCategoryFeed(header, categoryId, from, to);
    }

    public Observable<List<ImageResponse>> getMyCollection() {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        String header = createHeader(
                OzomeApiService.URL_PERSONAL,
                Strings.EMPTY,
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.getMyCollection(header);
    }

    public Observable<String> like(LikeRequest likeRequest) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        String header = createHeader(
                OzomeApiService.URL_SEND_ACTIONS,
                new Gson().toJson(likeRequest),
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.postLike(header, likeRequest);
    }

    public Observable<String> dislike(DislikeRequest dislikeRequest) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        String header = createHeader(
                OzomeApiService.URL_SEND_ACTIONS,
                new Gson().toJson(dislikeRequest),
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.postDislike(header, dislikeRequest);
    }

    public Observable<String> hide(HideRequest hideRequest) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        String header = createHeader(
                OzomeApiService.URL_SEND_ACTIONS,
                new Gson().toJson(hideRequest),
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.postHide(header, hideRequest);
    }

    public Observable<String> postShare(ShareRequest shareRequest) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        String header = createHeader(
                OzomeApiService.URL_SEND_ACTIONS,
                new Gson().toJson(shareRequest),
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.postShare(header, shareRequest);
    }

    public Observable<String> pin(CategoryPinRequest categoryPinRequest) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        String header = createHeader(
                OzomeApiService.URL_SEND_ACTIONS,
                new Gson().toJson(categoryPinRequest),
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.pin(header, categoryPinRequest);
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
        String header = createHeader(
                OzomeApiService.URL_CONFIG,
                Strings.EMPTY,
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        ozomeApiService.getConfig(header).
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
        final PackageRequest packageRequest = PackageRequest.create(messengers);
        String header = createHeader(
                OzomeApiService.URL_SEND_DATA,
                new Gson().toJson(packageRequest),
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.sendPackages(header, packageRequest);
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
        String header = createHeader(
                OzomeApiService.URL_CATEGORIES,
                Strings.EMPTY,
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.getCategories(header);
    }

    public Observable<List<ImageResponse>> getGoldFeed(final long categoryId, final int from, final int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }

        String url = insertUrlPath(OzomeApiService.URL_GOLDEN, String.valueOf(categoryId));
        Map<String, String> params = new HashMap<>();
        params.put(OzomeApiService.PARAM_FROM, String.valueOf(from));
        params.put(OzomeApiService.PARAM_TO, String.valueOf(to));
        url = insertUrlParam(url, params);
        String header = createHeader(
                url,
                Strings.EMPTY,
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.getGoldFeed(header, categoryId, from, to);
    }

    public Observable<RestRegistration> register() {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }

        final String uniqueDeviceId = DeviceManagerTools.getUniqueDeviceId(context);
        RequestDeviceId requestDeviceId = new RequestDeviceId(uniqueDeviceId);
        String deviceIdJson = new Gson().toJson(requestDeviceId);
        String header = createHeader(
                OzomeApiService.URL_REGISTRATION,
                deviceIdJson,
                OzomeApiService.REGISTRY_USER_KEY,
                OzomeApiService.REGISTRY_USER_SECRET,
                clock.unixTime()
        );
        return ozomeApiService.register(header, requestDeviceId);
    }

    private String createHeader(String url, String json, String userKey, String userSecret, long timestamp) {
        String signature = createSignature(url, json, userKey, userSecret, timestamp);
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(userKey);
        headerBuilder.append(Strings.GUP);
        headerBuilder.append(timestamp);
        headerBuilder.append(Strings.GUP);
        headerBuilder.append(signature);
        return headerBuilder.toString();
    }

    private String createSignature(String url, String json, String userKey, String userSecret, long timestamp) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(url);
        signatureBuilder.append(json);
        signatureBuilder.append(userKey);
        signatureBuilder.append(userSecret);
        signatureBuilder.append(timestamp);
        return Encoding.base64HmacSha256(signatureBuilder.toString());
    }

    private String insertUrlParam(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(isFirst ? "?" : "&");
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            if (isFirst) {
                isFirst = false;
            }
        }
        return builder.toString();
    }

    private String insertUrlPath(String url, String param) {
        // replace expression {value} in url on value;
        return url.replaceAll("\\{([^\\{\\}]+)\\}", String.valueOf(param));
    }
}
