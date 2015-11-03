package com.umad.wat.data.social;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.umad.wat.base.mvp.BaseActivity;
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
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialPresenter.getFBCallbackManager().onActivityResult(requestCode, resultCode, data);
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, socialPresenter.getVkCallback())) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
