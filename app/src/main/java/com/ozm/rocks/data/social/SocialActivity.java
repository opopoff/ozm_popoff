package com.ozm.rocks.data.social;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.ozm.R;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;

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
        VKSdk.initialize(socialPresenter, getResources().getString(R.string.vk_api_od));
        FacebookSdk.sdkInitialize(getApplicationContext());
        VKUIHelper.onCreate(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
        socialPresenter.getFBCallbackManager().onActivityResult(requestCode, resultCode, data);
    }
}
