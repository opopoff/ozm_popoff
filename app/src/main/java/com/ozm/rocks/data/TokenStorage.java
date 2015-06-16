package com.ozm.rocks.data;

import android.support.annotation.Nullable;

import com.ozm.rocks.data.prefs.BooleanPreference;
import com.ozm.rocks.data.prefs.ShowWidgetQualifier;
import com.ozm.rocks.data.prefs.StringPreference;
import com.ozm.rocks.data.prefs.UserKeyQualifier;
import com.ozm.rocks.data.prefs.UserSecretQualifier;
import com.ozm.rocks.ui.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class TokenStorage {

    private final StringPreference userKeyPreference;
    private final StringPreference userSecretPreference;
    private final BooleanPreference showWidgetPreference;
    private final BooleanPreference onBoardingPreference;

    @Inject
    TokenStorage(@UserKeyQualifier StringPreference userKeyPreference,
                 @UserSecretQualifier StringPreference userSecretPreference,
                 @ShowWidgetQualifier BooleanPreference showWidgetPreference,
                 @ShowWidgetQualifier BooleanPreference onBoardingPreference) {
        this.userKeyPreference = userKeyPreference;
        this.userSecretPreference = userSecretPreference;
        this.showWidgetPreference = showWidgetPreference;
        this.onBoardingPreference = onBoardingPreference;
    }

    public String getUserKey() {
        if (!isAuthorized())
            return null;
        return userKeyPreference.get();
    }

    @Nullable
    public String getUserSecret() {
        if (!isAuthorized())
            return null;
        return userSecretPreference.get();
    }

    public void putUserKey(String userKey) {
        userKeyPreference.set(userKey);
    }

    public void putUserSecret(String userSecret) {
        userSecretPreference.set(userSecret);
    }

    public boolean isAuthorized() {
        return userKeyPreference.get() != null && userSecretPreference.get() != null;
    }

    public void showWidget(boolean show) {
        showWidgetPreference.set(show);
    }

    public boolean isShowWidget() {
        return showWidgetPreference.get();
    }

    public boolean isOnBoarding() {
        return onBoardingPreference.get();
    }

    public void setOnBoardingPreference() {
        onBoardingPreference.set(false);
    }

    public void clear() {
        userKeyPreference.delete();
        userSecretPreference.delete();
    }
}
