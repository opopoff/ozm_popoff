package com.ozm.rocks.ui.screen.pushwoosh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.arellomobile.android.push.PushManager;

import org.json.JSONObject;

import timber.log.Timber;

public class PushwooshNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;

        Timber.d("OzomePushWoosh: catch %s intent receiver", intent == null ? "empty" : "not empty");

        //Let Pushwoosh SDK to pre-handling push (Pushwoosh track stats, opens rich pages, etc.).
        //It will return Bundle with a push notification data
        Bundle pushBundle = PushManager.preHandlePush(context, intent);
        if(pushBundle == null)
            return;

        //get push bundle as JSON object
        JSONObject dataObject = PushManager.bundleToJSON(pushBundle);

        //Get default launcher intent for clarity
        Intent launchIntent  = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        launchIntent.addCategory("android.intent.category.LAUNCHER");

        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //Put push notifications payload in Intent
        launchIntent.putExtras(pushBundle);
        launchIntent.putExtra(PushManager.PUSH_RECEIVE_EVENT, dataObject.toString());

        //Start activity!
        context.startActivity(launchIntent);

        //Let Pushwoosh SDK post-handle push (track stats, etc.)
        PushManager.postHandlePush(context, intent);
    }
}
