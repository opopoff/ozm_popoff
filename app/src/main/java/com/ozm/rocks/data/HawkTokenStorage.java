package com.ozm.rocks.data;

import android.app.Application;
import android.support.annotation.Nullable;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.LogLevel;
import com.ozm.BuildConfig;

import timber.log.Timber;

final class HawkTokenStorage implements TokenStorage {
    private static final String PASSWORD = "pUPuswud27huZeR7";
    private static final String API_ID_KEY = HawkTokenStorage.class.getName() + ".apiId";
    private static final String API_TOKEN_KEY = HawkTokenStorage.class.getName() + ".apiToken";

    HawkTokenStorage(Application application) {
        Hawk.init(application, PASSWORD, BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE);
        Timber.d("Hawk successfully initialized");
    }

    @Override
    public long apiId() {
        return Hawk.get(API_ID_KEY);
    }

    @Nullable
    @Override
    public String apiToken() {
        if (!isAuthorized())
            return null;
        return Hawk.get(API_TOKEN_KEY);
    }

    @Override
    public void putApiId(long apiId) {
        Hawk.put(API_ID_KEY, apiId);
    }

    @Override
    public void putApiToken(String apiToken) {
        Hawk.put(API_TOKEN_KEY, apiToken);
    }

    @Override
    public boolean isAuthorized() {
        return Hawk.contains(API_ID_KEY) && Hawk.contains(API_TOKEN_KEY);
    }

    @Override
    public void clear() {
        Hawk.remove(API_ID_KEY, API_TOKEN_KEY);
    }
}
