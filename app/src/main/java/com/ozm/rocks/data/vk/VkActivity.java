package com.ozm.rocks.data.vk;

import android.content.Intent;
import android.os.Bundle;

import com.ozm.R;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;

import javax.inject.Inject;

/**
 * Created by Danil on 16.06.2015.
 */
public abstract class VkActivity extends BaseActivity {

    @Inject
    VkPresenter vkPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKSdk.initialize(vkPresenter, getResources().getString(R.string.vk_api_od));
        VKUIHelper.onCreate(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }
}
