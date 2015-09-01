package com.ozm.fun.data;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.ozm.fun.ApplicationScope;
import com.ozm.fun.data.api.response.PackageRequest;
import com.ozm.fun.data.prefs.BooleanPreference;
import com.ozm.fun.data.prefs.ConfigQualifier;
import com.ozm.fun.data.prefs.CreateAlbumQualifier;
import com.ozm.fun.data.prefs.FeedPromptQualifier;
import com.ozm.fun.data.prefs.IntPreference;
import com.ozm.fun.data.prefs.OnBoardingGoldFourLoadQualifier;
import com.ozm.fun.data.prefs.OnBoardingQualifier;
import com.ozm.fun.data.prefs.PersonalPopupShowed;
import com.ozm.fun.data.prefs.SendFriendDialogQualifier;
import com.ozm.fun.data.prefs.SharePicsCounterQualifier;
import com.ozm.fun.data.prefs.ShowWidgetQualifier;
import com.ozm.fun.data.prefs.StartApplicationCounterQualifier;
import com.ozm.fun.data.prefs.StringPreference;
import com.ozm.fun.data.prefs.UpFolderQualifier;
import com.ozm.fun.data.prefs.UserKeyQualifier;
import com.ozm.fun.data.prefs.UserSecretQualifier;
import com.ozm.fun.data.prefs.VkUserProfileQualifier;
import com.ozm.fun.util.Strings;

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
    private final StringPreference vkUserProfilePreference;
    private final StringPreference configPreference;
    private final IntPreference sendFriendDialogPreference;

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
                 @SharePicsCounterQualifier IntPreference sharePicsCounterPreference,
                 @VkUserProfileQualifier StringPreference vkUserProfilePreference,
                 @ConfigQualifier StringPreference configPreference,
                 @SendFriendDialogQualifier IntPreference sendFriendDialogPreference) {

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
        this.vkUserProfilePreference = vkUserProfilePreference;
        this.configPreference = configPreference;
        this.sendFriendDialogPreference = sendFriendDialogPreference;
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

    public void setVkData(PackageRequest.VkData vkData) {
        final String json = new Gson().toJson(vkData);
        vkUserProfilePreference.set(json);
    }

    public PackageRequest.VkData getVkData() {
        final String json = vkUserProfilePreference.get();
        if (Strings.isBlank(json)) return null;
        return new Gson().fromJson(json, PackageRequest.VkData.class);
    }

    public String getConfigString() {
        return configPreference.get();
    }

    public void setConfigString(String config) {
        configPreference.set(config);
    }

    public void setSendFriendDialogPreference(int sendFriendDialog){
        sendFriendDialogPreference.set(sendFriendDialog);
    }

    public int getSendFriendDialogPreference() {
        return sendFriendDialogPreference.get();
    }
}
