package com.ozm.fun.ui.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WidgetReceiver extends BroadcastReceiver {

    private static final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_COMPLETE.equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, WidgetBootService.class);
            context.startService(pushIntent);
        }
    }
}
