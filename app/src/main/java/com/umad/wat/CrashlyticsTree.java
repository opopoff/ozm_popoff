package com.umad.wat;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.umad.BuildConfig;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class CrashlyticsTree extends Timber.Tree {

  public CrashlyticsTree(Context context) {
    Fabric.with(context, new Crashlytics());

    // Put some debug variables for every crash
    // CrashlyticsCore core = Crashlytics.getInstance().core;
    // core.setString("Git SHA", BuildConfig.GIT_SHA);
  }

  @Override
  protected void log(int priority, String tag, String message, Throwable t) {
    if (priority == Log.VERBOSE && !OzomeApplication.RELEASE.equals(BuildConfig.BUILD_TYPE)
            && !OzomeApplication.STAGE.equals(BuildConfig.BUILD_TYPE)) {
      //Don't log verbose messages and avoid not release builds to send crash to crashlytics
      return;
    }

    CrashlyticsCore core = Crashlytics.getInstance().core;
    core.log(Log.ERROR, tag, message);

    if (t != null && priority == Log.ERROR) {
      core.logException(t);
    }
  }
}