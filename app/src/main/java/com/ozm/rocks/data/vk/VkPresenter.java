package com.ozm.rocks.data.vk;

import com.ozm.rocks.ui.ApplicationScope;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;

import javax.inject.Inject;

/**
 * Created by Danil on 10.06.2015.
 */
@ApplicationScope
public class VkPresenter extends VKSdkListener {
    private VkInterface vkInterface;

    public VkInterface getVkInterface() {
        return vkInterface;
    }

    public void setVkInterface(VkInterface vkInterface) {
        this.vkInterface = vkInterface;
    }

    @Inject
    public VkPresenter() {
    }

    @Override
    public void onCaptchaError(VKError vkError) {
        if (vkInterface != null) {
            vkInterface.onCaptchaError(vkError);
        }
    }

    @Override
    public void onTokenExpired(VKAccessToken vkAccessToken) {
        if (vkInterface != null) {
            vkInterface.onTokenExpired(vkAccessToken);
        }
    }

    @Override
    public void onAccessDenied(VKError vkError) {
        if (vkInterface != null) {
            vkInterface.onAccessDenied(vkError);
        }
    }
}
