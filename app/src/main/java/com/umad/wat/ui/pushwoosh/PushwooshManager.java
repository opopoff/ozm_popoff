package com.umad.wat.ui.pushwoosh;

import android.content.Context;
import android.support.annotation.NonNull;

import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.SendPushTagsCallBack;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class PushwooshManager {

    public static void sendTags(@NonNull Context context, @NonNull HashMap<String, Object> pushwooshTags) {
        PushManager.sendTags(context.getApplicationContext(), pushwooshTags, new SendPushTagsCallBack() {
            @Override
            public void taskStarted() {
                // nothing;
            }

            @Override
            public void onSentTagsSuccess(Map<String, String> map) {
                Timber.d("PushManager.sendTags success!");
            }

            @Override
            public void onSentTagsError(Exception e) {
                Timber.w(e, "PushManager.sendTags failed!");
            }
        });
    }
}
