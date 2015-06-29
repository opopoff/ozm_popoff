package com.ozm.rocks.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.ozm.rocks.base.ActivityConnector;
import com.ozm.rocks.util.DeviceManagerTools;

import javax.inject.Inject;

@ApplicationScope
public class ApplicationSwitcher extends ActivityConnector<Activity> {

    @Inject
    public ApplicationSwitcher() {
    }

    public void openFeedbackEmailApplication() {
        final Activity activity = getAttachedObject();
        if (activity == null) return;

        String msg = "Short about device:\n" + DeviceManagerTools.getDetailDeviceInfo() + "\n----\n";

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "zamesin.ivan@gmail.com", null));
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ozm: feedback");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
        activity.startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
    }
}
