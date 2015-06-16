package com.ozm.rocks.data.vk;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKError;

/**
 * Created by Danil on 10.06.2015.
 */
public interface VkInterface {
    void onCaptchaError(VKError vkError);

    void onTokenExpired(VKAccessToken vkAccessToken);

    void onAccessDenied(VKError vkError);
}
