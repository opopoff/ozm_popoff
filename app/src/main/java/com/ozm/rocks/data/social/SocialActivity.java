package com.ozm.rocks.data.social;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.vk.sdk.VKSdk;

import javax.inject.Inject;

/**
 * Created by Danil on 16.06.2015.
 */
public abstract class SocialActivity extends BaseActivity {

    @Inject
    SocialPresenter socialPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKSdk.initialize(this.getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
//        VKUIHelper.onCreate(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        VKUIHelper.onResume(this);
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socialPresenter.setVkCallback(null);
//        VKSdk.instance().setSdkListener(null);
//        VKUIHelper.onDestroy(this);
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
        socialPresenter.getFBCallbackManager().onActivityResult(requestCode, resultCode, data);
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, socialPresenter.getVkCallback())) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
