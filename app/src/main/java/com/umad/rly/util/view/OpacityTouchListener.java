package com.umad.rly.util.view;

import android.view.MotionEvent;
import android.view.View;

import timber.log.Timber;

/**
 * Created by cosic on 30/07/14;
 */
public class OpacityTouchListener implements View.OnTouchListener {
    private static final float OPACITY_SWITCH_OFF = 1.0f;
    private static final float OPACITY_NORMAL_STATE = OPACITY_SWITCH_OFF;
    private static final float OPACITY_TOUCH_STATE = 0.5f;

    private float mTouchOpacityValue = OPACITY_TOUCH_STATE;
    private float mNormalOpacityValue = OPACITY_NORMAL_STATE;

    private TouchListener listener;

    public OpacityTouchListener(TouchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setAlpha(mTouchOpacityValue);
//                v.performClick();
                Timber.i("ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                v.setAlpha(mNormalOpacityValue);
                Timber.i("ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha(mNormalOpacityValue);
                Timber.i("ACTION_CANCEL");
                break;
            default:
                break;
        }
        if (listener != null) {
            return listener.onTouch(v, event);
        }
        return false;
    }

    public void setTouchOpacityValue(float touchOpacityValue) {
        this.mTouchOpacityValue = touchOpacityValue;
    }

    public void setNormalOpacityValue(float normalOpacityValue) {
        this.mNormalOpacityValue = normalOpacityValue;
    }

    public static interface TouchListener {
        boolean onTouch(View v, MotionEvent event);
    }
}
