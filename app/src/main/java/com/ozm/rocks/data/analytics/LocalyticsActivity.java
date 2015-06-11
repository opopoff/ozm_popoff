package com.ozm.rocks.data.analytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.localytics.android.Localytics;
import com.ozm.BuildConfig;

public abstract class LocalyticsActivity extends FragmentActivity {

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // If you're using Localytics Push Messaging
        final String projectNumber = BuildConfig.PROJECT_NUMBER;
        Localytics.registerPush(projectNumber);

        // Activity Creation Code
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onResume()
    {
        super.onResume();

        Localytics.openSession();
        Localytics.upload();

        if (this instanceof FragmentActivity)
        {
            Localytics.setInAppMessageDisplayActivity((FragmentActivity) this);
        }

        Localytics.handleTestMode(getIntent());
    }

    public void onPause()
    {
        if (this instanceof FragmentActivity)
        {
            Localytics.dismissCurrentInAppMessage();
            Localytics.clearInAppMessageDisplayActivity();
        }

        Localytics.closeSession();
        Localytics.upload();

        super.onPause();
    }

}
