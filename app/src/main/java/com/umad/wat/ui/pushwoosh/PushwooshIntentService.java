package com.umad.wat.ui.pushwoosh;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import com.arellomobile.android.push.PushHandlerActivity;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.GeneralUtils;
import com.arellomobile.android.push.utils.Log;
import com.arellomobile.android.push.utils.PreferenceUtils;
import com.arellomobile.android.push.utils.notification.AbsNotificationFactory;
import com.arellomobile.android.push.utils.notification.DefaultNotificationFactory;

import timber.log.Timber;

/**
 * We receive pushwoosh gcm message and extract image url.
 * If push have url than replace notification by custom view,
 * otherwise used default pushwoosh notification.
 * As background used jpeg 1384×225 px image;
 */

public class PushwooshIntentService extends com.arellomobile.android.push.PushGCMIntentService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        Timber.d("OzomePushWoosh: catch %s intent service", intent == null ? "empty" : "not empty");
        if (intent != null) {
            final Bundle extras = intent.getExtras();
            final String userdata = extras.getString("u");
            final String url = extras.getString("l");
            Timber.d("OzomePushWoosh: url:%s", url);
            Timber.d("OzomePushWoosh: userdata:%s", userdata);
            Timber.d("OzomePushWoosh: catch %s extras service", extras == null ? "empty" : "not empty");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.info("GCMIntentService", "Received message");
        generateNotification(context, intent);
    }

    /**
     * Method was to copied from PushServiceHelper.generateNotification(Context var0, Intent var1);
     */
    private void generateNotification(Context var0, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            PushwooshData pushwooshData = new PushwooshData(bundle);
            if(pushwooshData.isContainPushwooshKey()) {
                pushwooshData.setAppOnForeground(GeneralUtils.isAppOnForeground(var0));
                pushwooshData.setVibrateType(PreferenceUtils.getVibrateType(var0));
                pushwooshData.setSoundType(PreferenceUtils.getSoundType(var0));

                boolean var4;
                Intent var5;
                try {
                    var4 = true;
                    ApplicationInfo var7 = var0.getPackageManager().getApplicationInfo(var0.getPackageName(), 128);
                    String var8 = var7.metaData.getString("PW_NOTIFICATION_RECEIVER");
                    Class var6 = Class.forName(var8);
                    var5 = new Intent(var0, var6);
                } catch (Exception var9) {
                    var4 = false;
                    var5 = new Intent(var0, PushHandlerActivity.class);
                    var5.addFlags(603979776);
                }

                pushwooshData.setUseIntentReceiver(var4);
                var5.putExtra("pushBundle", bundle);

                if (pushwooshData.getUserdata() != null && pushwooshData.getUserdata().contains("url")) {
                    PushwooshNotificationFactory var10 = getNotificationFactory(var0);
                    var10.notify(var0, bundle, pushwooshData, var5);
                } else {
                    AbsNotificationFactory var10 = getDefaultNotificationFactory(var0);
                    var10.notify(var0, bundle, pushwooshData, var5);
                }
            }
        }
    }

    /**
     * Method was to copied from PushServiceHelper.getNotificationFactory(Context var0);
     */
    private static PushwooshNotificationFactory getNotificationFactory(Context var0) {
        AbsNotificationFactory var1 = PushManager.getInstance(var0).getNotificationFactory();
        return (PushwooshNotificationFactory)(var1 != null?var1:new PushwooshNotificationFactory());
    }

    private static AbsNotificationFactory getDefaultNotificationFactory(Context var0) {
        AbsNotificationFactory var1 = PushManager.getInstance(var0).getNotificationFactory();
        return (AbsNotificationFactory)(var1 != null?var1:new DefaultNotificationFactory());
    }

}
