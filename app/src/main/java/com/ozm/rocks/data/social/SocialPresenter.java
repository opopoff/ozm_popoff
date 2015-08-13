package com.ozm.rocks.data.social;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.ozm.rocks.ApplicationScope;
//import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.api.VKError;

import javax.inject.Inject;

/**
 * Created by Danil on 10.06.2015.
 */
@ApplicationScope
//public class SocialPresenter extends VKSdkListener {
public class SocialPresenter {
    private VKCallback<VKAccessToken> vkCallback;
    private final CallbackManager callbackManager = CallbackManager.Factory.create();

    public VKCallback<VKAccessToken> getVkCallback() {
        return vkCallback;
    }

    public void setVkCallback(VKCallback<VKAccessToken> vkCallback) {
        this.vkCallback = vkCallback;
    }


    @Inject
    public SocialPresenter() {
    }

    public CallbackManager getFBCallbackManager() {
        return callbackManager;
    }
}
