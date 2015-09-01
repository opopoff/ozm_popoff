package com.ozm.fun.ui.pushwoosh;

import android.os.Bundle;

import com.arellomobile.android.push.utils.notification.PushData;

/**
 * Created by Danil on 31.07.2015.
 */
public class OzmPushData extends PushData {
    private String userdata;

    public OzmPushData(Bundle bundle) {
        super(bundle);
        userdata = bundle.getString("u");
    }

    public String getUserdata() {
        return userdata;
    }
}
