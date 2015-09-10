package com.umad.wat.base.tools;

import android.app.Activity;
import android.view.View;

import com.umad.wat.base.ActivityConnector;
import com.umad.wat.ApplicationScope;
import com.umad.wat.util.KeyboardTools;

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
