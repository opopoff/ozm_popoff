package com.ozm.rocks.data;

import android.support.annotation.Nullable;

import com.ozm.rocks.data.prefs.BooleanPreference;
import com.ozm.rocks.data.prefs.CreateAlbumQualifier;
import com.ozm.rocks.data.prefs.FeedPromptQualifier;
import com.ozm.rocks.data.prefs.IntPreference;
import com.ozm.rocks.data.prefs.OnBoardingGoldFourLoadQualifier;
import com.ozm.rocks.data.prefs.OnBoardingQualifier;
import com.ozm.rocks.data.prefs.PersonalPopupShowed;
import com.ozm.rocks.data.prefs.SharePicsCounterQualifier;
import com.ozm.rocks.data.prefs.ShowWidgetQualifier;
import com.ozm.rocks.data.prefs.StartApplicationCounterQualifier;
import com.ozm.rocks.data.prefs.StringPreference;
import com.ozm.rocks.data.prefs.UpFolderQualifier;
import com.ozm.rocks.data.prefs.UserKeyQualifier;
import com.ozm.rocks.data.prefs.UserSecretQualifier;
import com.ozm.rocks.ui.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class TokenStorage {

    private final StringPreference userKeyPreference;
    private final StringPreference userSecretPreference;
    private final BooleanPreference showWidgetPreference;
    private final IntPreference goldFourOnBoarding;
    private final BooleanPreference feedPromptPreference;
    private final BooleanPreference onBoardingPreference;
    private final BooleanPreference createAlbumPreference;
    private final BooleanPreference upFolderPreference;
    private final BooleanPreference personalPopupShowed;
    private final IntPreference startAppCounterPreference;
    private final IntPreference sharePicsCounterPreference;

    @Inject
    TokenStorage(@UserKeyQualifier StringPreference userKeyPreference,
                 @UserSecretQualifier StringPreference userSecretPreference,
                 @ShowWidgetQualifier BooleanPreference showWidgetPreference,
                 @OnBoardingGoldFourLoadQualifier IntPreference goldFourOnBoarding,
                 @FeedPromptQualifier BooleanPreference feedPromptPreference,
                 @OnBoardingQualifier BooleanPreference onBoardingPreference,
                 @CreateAlbumQualifier BooleanPreference createAlbumPreference,
                 @UpFolderQualifier BooleanPreference upFolderPreference,
                 @PersonalPopupShowed BooleanPreference personalPopupShowed,
                 @StartApplicationCounterQualifier IntPreference startAppCounterPreference,
                 @SharePicsCounterQualifier IntPreference sharePicsCounterPreference) {

        this.userKeyPreference = userKeyPreference;
        this.userSecretPreference = userSecretPreference;
        this.showWidgetPreference = showWidgetPreference;
        this.goldFourOnBoarding = goldFourOnBoarding;
        this.feedPromptPreference = feedPromptPreference;
        this.onBoardingPreference = onBoardingPreference;
        this.createAlbumPreference = createAlbumPreference;
        this.upFolderPreference = upFolderPreference;
        this.personalPopupShowed = personalPopupShowed;
        this.startAppCounterPreference = startAppCounterPreference;
        this.sharePicsCounterPreference = sharePicsCounterPreference;
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

    public int getGoldFourOnBoarding() {
        return goldFourOnBoarding.get();
    }

    public void upGoldFirstOnBoarding() {
        goldFourOnBoarding.set(goldFourOnBoarding.get() + 1);
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

    public boolean isUpFolder() {
        return upFolderPreference.get();
    }

    public void setUpFolder() {
        upFolderPreference.set(true);
    }

    public boolean isPersonalPopupShowed() {
        return personalPopupShowed.get();
    }

    public void setPersonalPopupShowed() {
        personalPopupShowed.set(true);
    }

    public int getStartAppCounter() {
        return startAppCounterPreference.get();
    }

    public void setStartAppCounter(int count) {
        startAppCounterPreference.set(count);
    }

    public int getSharePicCounter() {
        return sharePicsCounterPreference.get();
    }

    public void setSharePicCounter(int count) {
        sharePicsCounterPreference.set(count);
    }

    public void clear() {
        userKeyPreference.delete();
        userSecretPreference.delete();
    }
}
