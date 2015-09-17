package com.umad.wat;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class ReleaseTimberTree extends Timber.Tree {

    @Override
    public void v(String message, Object... args) {

    }

    @Override
    public void v(Throwable t, String message, Object... args) {
        Crashlytics.logException(t);
    }

    @Override
    public void d(String message, Object... args) {

    }

    @Override
    public void d(Throwable t, String message, Object... args) {
        Crashlytics.logException(t);
    }

    @Override
    public void i(String message, Object... args) {

    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        Crashlytics.logException(t);
    }

    @Override
    public void w(String message, Object... args) {

    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        Crashlytics.logException(t);
    }

    @Override
    public void e(String message, Object... args) {

    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        Crashlytics.logException(t);
    }

    @Override
    protected void log(int i, String s, String s1, Throwable throwable) {

    }
}

