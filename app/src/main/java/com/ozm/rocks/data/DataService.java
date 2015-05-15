package com.ozm.rocks.data;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ozm.rocks.data.api.OzomeApiService;
import com.ozm.rocks.data.api.model.Config;
import com.ozm.rocks.data.api.response.ActivationResponse;
import com.ozm.rocks.data.api.response.AuthResponse;
import com.ozm.rocks.data.api.response.ConfigResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.Messenger;
import com.ozm.rocks.data.api.response.PackageRequest;
import com.ozm.rocks.data.api.response.Response;
import com.ozm.rocks.data.api.response.RestConfig;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.Strings;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@ApplicationScope
public class DataService {
    public static final String NO_INTERNET_CONNECTION = "No internet connection";

    private final ConnectivityManager connectivityManager;
    private final OzomeApiService mOzomeApiService;
    private final TokenStorage tokenStorage;

    @Inject
    public DataService(Application application, OzomeApiService ozomeApiService,
                       TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.mOzomeApiService = ozomeApiService;
    }

    public Observable<Boolean> signIn(String email, String password) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        Timber.d("Signing in with %s", email);
        return mOzomeApiService.signIn(email, password).
                map(new Func1<AuthResponse, Boolean>() {
                    @Override
                    public Boolean call(AuthResponse authResponse) {
                        if (authResponse.hasError())
                            return false;
                        final AuthResponse.User user = authResponse.user;
                        if (user != null) {
                            if (!Strings.isBlank(user.apiToken)) {
                                tokenStorage.putApiId(user.apiId);
                                tokenStorage.putApiToken(user.apiToken);
                                return true;
                            }
                            return false;
                        }
                        return false;
                    }
                });
    }

    public Observable<ActivationResponse> activateByQr(String qrCouponCode) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return mOzomeApiService.activateByQr(qrCouponCode);
    }

    public Observable<ActivationResponse> activateByBarcode(String barcode) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return mOzomeApiService.activateByBarcode(barcode);
    }

    public Observable<ActivationResponse> activateByCoupon(String coupon, String security) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return mOzomeApiService.activateByCoupon(coupon, security);
    }

    public Observable<ActivationResponse> search(String coupon) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return mOzomeApiService.search(coupon);
    }

    private boolean hasInternet() {
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Observable<List<ImageResponse>> getGeneralFeed(Integer from, Integer to) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return mOzomeApiService.getGeneralFeed(from, to);
    }

    public Observable<Config> getConfig() {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        return mOzomeApiService.getConfig().
                map(new Func1<RestConfig, Config>() {
                    @Override
                    public Config call(RestConfig restConfig) {
                        return Config.from(restConfig);
                    }
                });
    }

    public Observable<retrofit.client.Response> sendPackages(ArrayList<PInfo> pInfos) {
        if (!hasInternet()) {
            return Observable.error(new NetworkErrorException(NO_INTERNET_CONNECTION));
        }
        Timber.d("hasInternet");
        List<Messenger> messengers = new ArrayList<>();
//        messengers.add(Messenger.create(pInfos.get(0).getPname()));
        for (PInfo pInfo : pInfos)
        {
            messengers.add(Messenger.create(pInfo.getPname()));
        }
        Timber.d("map complete");
        return mOzomeApiService.sendPackages(PackageRequest.create(messengers));
    }
}
