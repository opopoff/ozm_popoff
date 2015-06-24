package com.ozm.rocks.data.social;

import com.facebook.CallbackManager;
import com.ozm.rocks.ui.ApplicationScope;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;

import javax.inject.Inject;

/**
 * Created by Danil on 10.06.2015.
 */
@ApplicationScope
public class SocialPresenter extends VKSdkListener {
    private VkInterface vkInterface;
    private final CallbackManager callbackManager = CallbackManager.Factory.create();

    public VkInterface getVkInterface() {
        return vkInterface;
    }

    public void setVkInterface(VkInterface vkInterface) {
        this.vkInterface = vkInterface;
    }


    @Inject
    public SocialPresenter() {
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

    @Override
    public void onReceiveNewToken(VKAccessToken newToken) {
        super.onReceiveNewToken(newToken);
        if (vkInterface != null) {
            vkInterface.onReceiveNewToken(newToken);
        }
    }

    @Override
    public void onAcceptUserToken(VKAccessToken token) {
        super.onAcceptUserToken(token);
        if (vkInterface != null) {
            vkInterface.onAcceptUserToken(token);
        }
    }

    @Override
    public void onRenewAccessToken(VKAccessToken token) {
        super.onRenewAccessToken(token);
        if (vkInterface != null) {
            vkInterface.onRenewAccessToken(token);
        }
    }

    public CallbackManager getFBCallbackManager() {
        return callbackManager;
    }
}
