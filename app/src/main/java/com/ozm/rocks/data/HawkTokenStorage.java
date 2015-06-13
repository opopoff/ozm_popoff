package com.ozm.rocks.data;

import android.app.Application;
import android.support.annotation.Nullable;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.LogLevel;
import com.ozm.BuildConfig;
import com.ozm.rocks.util.Timestamp;

import timber.log.Timber;

final class HawkTokenStorage implements TokenStorage {
    private static final String PASSWORD = "pUPuswud27huZeR7";
    private static final String USER_KEY = HawkTokenStorage.class.getName() + ".userKey";
    private static final String USER_SECRET = HawkTokenStorage.class.getName() + ".userSecret";
    private static final String SHOW_WIDGET = HawkTokenStorage.class.getName() + ".showWidget";

    HawkTokenStorage(Application application) {
        final long timestamp = Timestamp.getUTC();
        Hawk.init(application, PASSWORD, BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE);
        Timber.d("Hawk successfully initialized for %d", Timestamp.getUTC() - timestamp);
    }

    @Override
    public String getUserKey() {
        if (!isAuthorized())
            return null;
        return Hawk.get(USER_KEY);
    }

    @Nullable
    @Override
    public String getUserSecret() {
        if (!isAuthorized())
            return null;
        return Hawk.get(USER_SECRET);
    }

    @Override
    public void putUserKey(String userKey) {
        Hawk.put(USER_KEY, userKey);
    }

    @Override
    public void putUserSecret(String userSecret) {
        Hawk.put(USER_SECRET, userSecret);
    }

    @Override
    public boolean isAuthorized() {
        return Hawk.contains(USER_KEY) && Hawk.contains(USER_SECRET);
    }

    @Override
    public void showWidget(boolean show) {
        Hawk.put(SHOW_WIDGET, show);
    }

    @Override
    public boolean isShowWidget() {
        return Hawk.get(SHOW_WIDGET, true);
    }

    @Override
    public void clear() {
        Hawk.remove(USER_KEY, USER_SECRET);
    }
}
