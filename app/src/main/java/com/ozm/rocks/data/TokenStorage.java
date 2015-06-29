package com.ozm.rocks.data;

import android.support.annotation.Nullable;

import com.ozm.rocks.data.prefs.BooleanPreference;
import com.ozm.rocks.data.prefs.CreateAlbumQualifier;
import com.ozm.rocks.data.prefs.FeedPromptQualifier;
import com.ozm.rocks.data.prefs.OnBoardingGoldFirstLoadQualifier;
import com.ozm.rocks.data.prefs.OnBoardingQualifier;
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
    private final BooleanPreference goldFirstOnBoarding;
    private final BooleanPreference feedPromptPreference;
    private final BooleanPreference onBoardingPreference;
    private final BooleanPreference createAlbumPreference;

    @Inject
    TokenStorage(@UserKeyQualifier StringPreference userKeyPreference,
                 @UserSecretQualifier StringPreference userSecretPreference,
                 @ShowWidgetQualifier BooleanPreference showWidgetPreference,
                 @OnBoardingGoldFirstLoadQualifier BooleanPreference goldFirstOnBoarding,
                 @FeedPromptQualifier BooleanPreference feedPromptPreference,
                 @OnBoardingQualifier BooleanPreference onBoardingPreference,
                 @CreateAlbumQualifier BooleanPreference createAlbumPreference) {
        this.userKeyPreference = userKeyPreference;
        this.userSecretPreference = userSecretPreference;
        this.showWidgetPreference = showWidgetPreference;
        this.goldFirstOnBoarding = goldFirstOnBoarding;
        this.feedPromptPreference = feedPromptPreference;
        this.onBoardingPreference = onBoardingPreference;
        this.createAlbumPreference = createAlbumPreference;
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

    public boolean getGoldFirstOnBoarding() {
        return goldFirstOnBoarding.get();
    }

    public void putGoldFirstOnBoarding(boolean b) {
        goldFirstOnBoarding.set(b);
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

    public boolean isFeedPromptShowed() {
        return feedPromptPreference.get();
    }

    public void setFeedPromptShowed() {
        feedPromptPreference.set(true);
    }

    public boolean isOnBoardingShowed() {
        return onBoardingPreference.get();
    }

    public void setOnBoardingShowed() {
        onBoardingPreference.set(true);
    }

    public boolean isCreateAlbum() {
        return createAlbumPreference.get();
    }

    public void setCreateAlbum(boolean b) {
        createAlbumPreference.set(b);
    }

    public void clear() {
        userKeyPreference.delete();
        userSecretPreference.delete();
    }
}
