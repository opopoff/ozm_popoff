package com.umad.wat.base.navigation.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;

import com.umad.wat.base.ActivityConnector;
import com.umad.wat.base.navigation.Screen;
import com.umad.wat.base.navigation.ScreenSwitcher;

import java.security.InvalidParameterException;


public class ActivityScreenSwitcher extends ActivityConnector<Activity> implements ScreenSwitcher {

    @Override
    public void open(Screen screen) {
        final Activity activity = getAttachedObject();
        if (activity == null) {
            return;
        }
        if (screen instanceof ActivityScreen) {
            ActivityScreen activityScreen = ((ActivityScreen) screen);
            Intent intent = activityScreen.intent(activity);
            ActivityCompat.startActivity(activity, intent, activityScreen.activityOptions(activity));
        } else {
            throw new InvalidParameterException("Only ActivityScreen objects allowed");
        }
    }

    @Override
    public void openForResult(Screen screen, int requestCode) {
        final Activity activity = getAttachedObject();
        if (activity == null) {
            return;
        }
        if (screen instanceof ActivityScreen) {
            ActivityScreen activityScreen = ((ActivityScreen) screen);
            Intent intent = activityScreen.intent(activity);
            ActivityCompat.startActivityForResult(activity, intent, requestCode, activityScreen.activityOptions
                    (activity));
        } else {
            throw new InvalidParameterException("Only ActivityScreen objects allowed");
        }
    }

    @Override
    public void goBackResult(int resultCode, Intent data) {
        final Activity activity = getAttachedObject();
        if (activity != null) {
            activity.setResult(resultCode, data);
            activity.onBackPressed();
        }
    }

    @Override
    public void setResult(int resultCode, Intent data) {
        final Activity activity = getAttachedObject();
        if (activity != null) {
            activity.setResult(resultCode, data);
        }
    }

    @Override
    public void goBack() {
        final Activity activity = getAttachedObject();
        if (activity != null) {
            activity.onBackPressed();
        }
    }
}
