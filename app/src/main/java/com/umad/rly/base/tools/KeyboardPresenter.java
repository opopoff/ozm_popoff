package com.umad.rly.base.tools;

import android.app.Activity;
import android.view.View;

import com.umad.rly.base.ActivityConnector;
import com.umad.rly.ApplicationScope;
import com.umad.rly.util.KeyboardTools;

import javax.inject.Inject;

@ApplicationScope
public class KeyboardPresenter extends ActivityConnector<Activity> {

    @Inject
    public KeyboardPresenter() {
    }

    public void hide() {
        Activity attachedObject = getAttachedObject();
        if (attachedObject == null) return;
        KeyboardTools.hideSoftKeyboard(attachedObject);
    }

    public void show(View view) {
        KeyboardTools.showSoftKeyboard(view);
    }
}
