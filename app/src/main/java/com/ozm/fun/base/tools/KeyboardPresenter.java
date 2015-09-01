package com.ozm.fun.base.tools;

import android.app.Activity;
import android.view.View;

import com.ozm.fun.base.ActivityConnector;
import com.ozm.fun.ApplicationScope;
import com.ozm.fun.util.KeyboardTools;

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
