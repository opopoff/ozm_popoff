package com.ozm.fun.data.social;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKError;

/**
 * Created by Danil on 10.06.2015.
 */
public interface VkInterface {
    void onCaptchaError(VKError vkError);

    void onTokenExpired(VKAccessToken vkAccessToken);

    void onAccessDenied(VKError vkError);

    void onReceiveNewToken(VKAccessToken newToken);

    void onAcceptUserToken(VKAccessToken token);

    void onRenewAccessToken(VKAccessToken token);
}
