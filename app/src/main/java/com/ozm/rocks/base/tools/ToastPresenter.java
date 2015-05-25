package com.ozm.rocks.base.tools;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.ozm.rocks.base.ActivityConnector;
import com.ozm.rocks.ui.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class ToastPresenter extends ActivityConnector<Activity> {

    @Inject
    public ToastPresenter() {
    }

    public void show(String msg) {
        final Activity activity = getAttachedObject();
        if (activity == null) return;
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

    public void show(@StringRes int msgRes) {
        final Activity activity = getAttachedObject();
        if (activity == null) return;
        Toast.makeText(activity, msgRes, Toast.LENGTH_LONG).show();
    }

}