package com.umad.wat.data;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.umad.R;
import com.umad.wat.ApplicationScope;
import com.umad.wat.data.api.OzomeApiService;
import com.umad.wat.data.api.ServerErrorException;
import com.umad.wat.data.api.model.Config;
import com.umad.wat.data.api.request.CategoryPinRequest;
import com.umad.wat.data.api.request.DislikeRequest;
import com.umad.wat.data.api.request.HideRequest;
import com.umad.wat.data.api.request.LikeRequest;
import com.umad.wat.data.api.request.RequestDeviceId;
import com.umad.wat.data.api.request.SettingRequest;
import com.umad.wat.data.api.request.ShareRequest;
import com.umad.wat.data.api.response.CategoryResponse;
import com.umad.wat.data.api.response.GifMessengerOrder;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.api.response.MessengerConfigs;
import com.umad.wat.data.api.response.MessengerOrder;
import com.umad.wat.data.api.response.RestConfig;
import com.umad.wat.data.api.response.RestRegistration;
import com.umad.wat.data.model.PInfo;
import com.umad.wat.data.rx.RequestFunction;
import com.umad.wat.ui.message.NoInternetPresenter;
import com.umad.wat.ui.pushwoosh.PushwooshManager;
import com.umad.wat.util.DeviceManagerTools;
import com.umad.wat.util.Encoding;
import com.umad.wat.util.PackageManagerTools;
import com.umad.wat.util.Strings;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;
import rx.Observable;
import rx.Observable.Transformer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import timber.log.Timber;

@ApplicationScope
public class DataService {
    public static final String NO_INTERNET_CONNECTION = "No internet connection";
    private static final int MAX_COUNT_APP_ON_SCREEN = 3;
    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final OzomeApiService ozomeApiService;
    private final NoInternetPresenter noInternetPresenter;
    private final FileService fileService;
    private final PackageManagerTools packageManagerTools;
    private final TokenStorage tokenStorage;
    private final Clock clock;
    private final Picasso picasso;

    @Nullable private ReplaySubject<ArrayList<PInfo>> packagesReplaySubject;
    @Nullable private WeakReference<Config> mConfig;

    @Inject
    public DataService(Application application, Clock clock, TokenStorage tokenStorage,
                       FileService fileService, PackageManagerTools packageManagerTools,
                       NoInternetPresenter noInternetPresenter, OzomeApiService ozomeApiService,
                       Picasso picasso, GsonConverter gsonConverter) {
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = application;
        this.fileService = fileService;
        this.noInternetPresenter = noInternetPresenter;
        this.packageManagerTools = packageManagerTools;
        this.ozomeApiService = ozomeApiService;
        this.tokenStorage = tokenStorage;
        this.clock = clock;
        this.picasso = picasso;
    }

    public Observable<List<ImageResponse>> getCategoryFeed(final long categoryId, int page) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }

        final int part = context.getResources().getInteger(R.integer.page_part_count);
        final int from = page * part;
        final int to = (page + 1) * part;

        String url = insertUrlPath(OzomeApiService.URL_CATEGORY_FEED, String.valueOf(categoryId));
        Map<String, String> params = new LinkedHashMap<>();
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
        return ozomeApiService.getCategoryFeed(header, categoryId, from, to)
                .compose(this.<List<ImageResponse>>wrapTransformer());
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
        return ozomeApiService.getPersonalFeed(header)
                .compose(this.<List<ImageResponse>>wrapTransformer());
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
        return ozomeApiService.postLike(header, likeRequest)
                .compose(this.<String>wrapTransformer());
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
        return ozomeApiService.postDislike(header, dislikeRequest)
                .compose(this.<String>wrapTransformer());
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
        return ozomeApiService.postHide(header, hideRequest)
                .compose(this.<String>wrapTransformer());
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
        return ozomeApiService.postShare(header, shareRequest)
                .compose(this.<String>wrapTransformer());
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
        return ozomeApiService.pin(header, categoryPinRequest)
                .compose(this.<String>wrapTransformer());
    }

    public Observable<String> sendCensorshipSetting(SettingRequest settingRequest) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        String header = createHeader(
                OzomeApiService.URL_SEND_SETTINGS,
                new Gson().toJson(settingRequest),
                tokenStorage.getUserKey(),
                tokenStorage.getUserSecret(),
                clock.unixTime()
        );
        return ozomeApiService.sendCensorshipSetting(header, settingRequest)
                .compose(this.<String>wrapTransformer());
    }

    private boolean hasInternet() {
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Observable<Config> getConfig() {

        if (mConfig != null && mConfig.get() != null) {
            return Observable.just(mConfig.get());
        }

        final Observable<Config> serverObservable = Observable.just(Boolean.TRUE)
                .flatMap(new Func1<Boolean, Observable<RestConfig>>() {
                    @Override
                    public Observable<RestConfig> call(Boolean aBoolean) {
                        final String header = createHeader(
                                OzomeApiService.URL_CONFIG,
                                Strings.EMPTY,
                                tokenStorage.getUserKey(),
                                tokenStorage.getUserSecret(),
                                clock.unixTime()
                        );
                        return ozomeApiService.getConfig(header);
                    }
                })
                .map(new Func1<RestConfig, Config>() {
                    @Override
                    public Config call(RestConfig restConfig) {
//                        tokenStorage.setConfigString(new Gson().toJson(restConfig));
                        if (restConfig.pushwooshTags != null && restConfig.pushwooshTags.size() > 0) {
                            PushwooshManager.sendTags(context.getApplicationContext(), restConfig.pushwooshTags);
                        }
                        return Config.merge(restConfig, tokenStorage.getConfigString(), "server");
                    }
                })
                .compose(this.<Config>wrapTransformer())
                .map(new Func1<Config, Config>() {
                    @Override
                    public Config call(Config config) {
                        mConfig = new WeakReference<Config>(config);
                        return config;
                    }
                });

        Observable<Config> storeConfigObservable = Observable.create(
                new RequestFunction<Config>() {
                    @Override
                    protected Config request() {
                        Gson gson = new Gson();
                        final String configString = tokenStorage.getConfigString();
                        if (Strings.isBlank(configString)) {
                            return null;
                        }
                        RestConfig restConfig = gson.fromJson(configString, RestConfig.class);
                        return Config.from(restConfig, "database");
                    }
                });

        return Observable.merge(storeConfigObservable, serverObservable);
    }

    public Observable<Boolean> createImage(final String url, final String sharingUrl, final String fileType) {
        return Observable.create(
                new RequestFunction<Boolean>() {
                    @Override
                    protected Boolean request() {
                        return fileService.createFile(url, fileType, false, tokenStorage.isCreateAlbum());
                    }
                })
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return fileService.createFile(sharingUrl, fileType, true, tokenStorage.isCreateAlbum());
                    }
                });
    }

    public Observable<Boolean> createImageFromCache(final ImageResponse image,
                                                    final MessengerConfigs config) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (image.isGIF && config != null && !config.supportsGIF) {
                    subscriber.onNext(fileService.createFile(image.videoUrl, "", true, tokenStorage.isCreateAlbum()));
                    subscriber.onCompleted();
                } else if (image.isGIF) {
                    fileService.createFileFromIon(image.url, image.imageType, tokenStorage.isCreateAlbum(), subscriber);
                } else {
                    subscriber.onNext(fileService.createFileFromPicasso(picasso, image.url,
                            image.imageType, tokenStorage.isCreateAlbum()));
                    subscriber.onCompleted();
                }
            }
        });
    }

    public Observable<Boolean> deleteImage(final ImageResponse image) {
        return Observable.create(new RequestFunction<Boolean>() {
            @Override
            protected Boolean request() {
                return fileService.deleteFile(image.url, image.imageType, tokenStorage.isCreateAlbum(), false);
            }
        }).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                return fileService.deleteFile(image.sharingUrl, image.imageType, tokenStorage.isCreateAlbum(), true);
            }
        });
    }

    public Observable<Boolean> deleteAllFromGallery() {
        return Observable.create(new RequestFunction<Boolean>() {
            @Override
            protected Boolean request() {
                return fileService.deleteAllFromGallery();
            }
        });
    }

//    public Observable<Response> sendPackages(final PackageRequest.VkData vkData) {
//        if (!hasInternet()) {
//            noInternetPresenter.showMessageWithTimer();
//            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
//        }
//        noInternetPresenter.hideMessage();
//
//        return getPackages().flatMap(new Func1<ArrayList<PInfo>, Observable<Response>>() {
//            @Override
//            public Observable<Response> call(ArrayList<PInfo> pInfos) {
//                final List<Messenger> messengers = new ArrayList<>();
//                for (PInfo pInfo : pInfos) {
//                    messengers.add(Messenger.create(pInfo.getPackageName()));
//                }
//                final String pushToken = PushManager.getPushToken(context.getApplicationContext());
//                Timber.d("PushManager: DataService pushToken=%s", pushToken);
//                final PackageRequest packageRequest = PackageRequest.create(messengers, vkData, pushToken);
//                String header = createHeader(
//                        OzomeApiService.URL_SEND_DATA,
//                        new Gson().toJson(packageRequest),
//                        tokenStorage.getUserKey(),
//                        tokenStorage.getUserSecret(),
//                        clock.unixTime()
//                );
//                return ozomeApiService.sendPackages(header, packageRequest)
//                        .compose(DataService.this.<Response>wrapTransformer());
//            }
//        });
//    }

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
        })
                .subscribeOn(Schedulers.io())
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
        return ozomeApiService.getCategories(header)
                .compose(this.<CategoryResponse>wrapTransformer());
    }

    public Observable<List<ImageResponse>> getGoldFeed(final long categoryId, int page) {
        final int part = context.getResources().getInteger(R.integer.page_part_count);
        final int from = page * part;
        final int to = (page + 1) * part;
        return getGoldFeed(categoryId, from, to);
    }

    public Observable<List<ImageResponse>> getGoldFeed(final long categoryId, int from, int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }

        String url = insertUrlPath(OzomeApiService.URL_GOLDEN, String.valueOf(categoryId));
        Map<String, String> params = new LinkedHashMap<>();
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
        return ozomeApiService.getGoldFeed(header, categoryId, from, to)
                .compose(this.<List<ImageResponse>>wrapTransformer());
    }

    public Observable<List<ImageResponse>> getGeneralFeed(int from, int to) {
        if (!hasInternet()) {
            noInternetPresenter.showMessageWithTimer();
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        Map<String, String> params = new LinkedHashMap<>();
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

    public Observable<ArrayList<PInfo>> getPInfos(final boolean isGIF, final boolean fromGeneral) {
        return Observable.zip(getPackages(), getConfig(), new Func2<ArrayList<PInfo>, Config, ArrayList<PInfo>>() {
            @Override
            public ArrayList<PInfo> call(ArrayList<PInfo> packages, Config config) {
                ArrayList<PInfo> pInfos = new ArrayList<>();
                if (isGIF) {
                    for (GifMessengerOrder mc : config.gifMessengerOrders()) {
                        for (PInfo p : packages) {
                            if (mc.applicationId.equals(p.getPackageName())) {
                                if (fromGeneral) {
                                    pInfos.add(p);
                                } else if (!mc.applicationId.equals(
                                        PackageManagerTools.Messanger.FACEBOOK_MESSANGER.getPackagename())
                                        && !mc.applicationId.equals(
                                        PackageManagerTools.Messanger.VKONTAKTE.getPackagename())) {
                                    pInfos.add(p);
                                }
                            }
                            if (pInfos.size() >= MAX_COUNT_APP_ON_SCREEN) {
                                break;
                            }
                        }
                        if (pInfos.size() >= MAX_COUNT_APP_ON_SCREEN) {
                            break;
                        }
                    }
                } else {
                    for (MessengerOrder mc : config.messengerOrders()) {
                        for (PInfo p : packages) {
                            if (mc.applicationId.equals(p.getPackageName())) {
                                if (fromGeneral) {
                                    pInfos.add(p);
                                } else if (!mc.applicationId.equals(
                                        PackageManagerTools.Messanger.FACEBOOK_MESSANGER.getPackagename())
                                        && !mc.applicationId.equals(
                                        PackageManagerTools.Messanger.VKONTAKTE.getPackagename())) {
                                    pInfos.add(p);
                                }
                                if (pInfos.size() >= MAX_COUNT_APP_ON_SCREEN) {
                                    break;
                                }
                            }
                        }
                        if (pInfos.size() >= MAX_COUNT_APP_ON_SCREEN) {
                            break;
                        }
                    }
                }
                return pInfos;
            }
        });

    }

    public Observable<RestRegistration> register() {
        Timber.d("NewConfig: wrapTransformer: throwable instanceof ServerErrorException. Call Register");
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
        return ozomeApiService.register(header, requestDeviceId).map(new Func1<RestRegistration, RestRegistration>() {
            @Override
            public RestRegistration call(RestRegistration restRegistration) {
                tokenStorage.putUserKey(restRegistration.key);
                tokenStorage.putUserSecret(restRegistration.secret);
                Timber.d("NewConfig: register()");
                return restRegistration;
            }
        });
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

//    private <T> Observable<T> wrapRequest(final Observable<T> observable) {
//
//        if (!hasInternet()) {
//            noInternetPresenter.showMessageWithTimer();
//            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
//        }
//
//        return observable.onErrorResumeNext(new Func1<Throwable, Observable<T>>() {
//            @Override
//            public Observable<T> call(Throwable throwable) {
//                if (throwable instanceof ServerErrorException) {
//                    ServerErrorException exception = (ServerErrorException) throwable;
//                    if (exception.getErrorCode() == ServerErrorException.ERROR_TOKEN_INVALID ||
//                            exception.getErrorCode() == ServerErrorException.ERROR_TOKEN_EXPIRED) {
//                        return register().flatMap(new Func1<RestRegistration, Observable<T>>() {
//                            @Override
//                            public Observable<T> call(RestRegistration restRegistration) {
//                                return observable;
//                            }
//                        });
//                    }
//                }
//                return Observable.error(throwable);
//            }
//        });
//    }

    /**
     * http://blog.danlew.net/2015/03/02/dont-break-the-chain/
     * Wrapper for each api request method for validation of apiToken
     * and call regist api method if token is invalid. After than
     * recall source request Observable by default;
     */
    private <T> Transformer<T, T> wrapTransformer() {

        return new Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> entireObservable) {

                if (!hasInternet()) {
                    noInternetPresenter.showMessageWithTimer();
                    return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
                }

                return entireObservable.retryWhen(new Func1<Observable<? extends Throwable>, Observable<T>>() {
                    @Override
                    public Observable<T> call(Observable<? extends Throwable> attempts) {
                        return attempts.flatMap(new Func1<Throwable, Observable<T>>() {
                            @Override
                            public Observable<T> call(Throwable throwable) {
                                if (throwable instanceof ServerErrorException) {
                                    ServerErrorException exception = (ServerErrorException) throwable;
                                    final int errorCode = exception.getErrorCode();
                                    Timber.d("NewConfig: wrapTransformer: throwable instanceof ServerErrorException - YES");
                                    if (errorCode == ServerErrorException.ERROR_TOKEN_INVALID ||
                                            errorCode == ServerErrorException.ERROR_TOKEN_EXPIRED) {
                                        Timber.d("NewConfig: wrapTransformer: retry call entireObservable");
                                        return register().flatMap(new Func1<RestRegistration, Observable<T>>() {
                                            @Override
                                            public Observable<T> call(RestRegistration restRegistration) {
                                                Timber.d("NewConfig: wrapTransformer: throwable instanceof ServerErrorException after Register");
                                                return entireObservable;
                                            }
                                        });
                                    }
                                }
                                return Observable.error(throwable);
                            }
                        });
                    }
                }).map(new Func1<T, T>() {
                    @Override
                    public T call(T t) {
                        Timber.d("NewConfig: wrapTransformer: throwable instanceof ServerErrorException after retryWhen");
                        return t;
                    }
                });
            }
        };
    }

    public Object convertResponseBodyToObject(@NonNull Response response,
                                              @NonNull Type type,
                                              @NonNull GsonConverter gsonConverter) throws ConversionException {
        TypedInput body = response.getBody();
        if (body == null) {
            return null;
        }
        return gsonConverter.fromBody(body, type);
    }

    private String insertUrlPath(String url, String param) {
        // replace expression {value} in url on value;
        return url.replaceAll("\\{([^\\{\\}]+)\\}", String.valueOf(param));
    }

    public static class EmptyConfigThrowable extends Throwable {
        public EmptyConfigThrowable() {
        }

        @Override
        public String toString() {
            return super.toString() + " Empty Config at SharedPreference store";
        }
    }
}
