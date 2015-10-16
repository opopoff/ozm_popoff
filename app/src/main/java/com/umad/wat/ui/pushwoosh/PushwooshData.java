package com.umad.wat.ui.pushwoosh;

import android.os.Bundle;

import com.arellomobile.android.push.utils.notification.PushData;

public class PushwooshData extends PushData {
    private String userdata;

    public PushwooshData(Bundle bundle) {
        super(bundle);
        userdata = bundle.getString("u");
    }

    public String getUserdata() {
        return userdata;
    }
}
