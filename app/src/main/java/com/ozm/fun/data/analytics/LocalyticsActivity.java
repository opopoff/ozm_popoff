package com.ozm.fun.data.analytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.localytics.android.Localytics;
import com.ozm.BuildConfig;

public abstract class LocalyticsActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If you're using Localytics Push Messaging
        final String projectNumber = BuildConfig.PROJECT_NUMBER;
        Localytics.registerPush(projectNumber);

        // Activity Creation Code
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onResume() {
        super.onResume();

        Localytics.openSession();
        Localytics.upload();
        Localytics.setInAppMessageDisplayActivity(this);
        Localytics.handleTestMode(getIntent());
    }

    public void onPause() {
        Localytics.dismissCurrentInAppMessage();
        Localytics.clearInAppMessageDisplayActivity();
        Localytics.closeSession();
        Localytics.upload();

        super.onPause();
    }

}
