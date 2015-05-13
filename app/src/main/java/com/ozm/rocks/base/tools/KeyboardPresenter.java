package com.ozm.rocks.base.tools;

import android.app.Activity;
import android.view.View;

import com.ozm.rocks.base.ActivityConnector;
import com.ozm.rocks.util.KeyboardTools;


public class KeyboardPresenter extends ActivityConnector<Activity> {

    public void hide() {
        Activity attachedObject = getAttachedObject();
        if (attachedObject == null) return;
        KeyboardTools.hideSoftKeyboard(attachedObject);
    }

    public void show(View view) {
        KeyboardTools.showSoftKeyboard(view);
    }
}
