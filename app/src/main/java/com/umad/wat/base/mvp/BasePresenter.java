package com.umad.wat.base.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public abstract class BasePresenter<V extends BaseView> {
    protected static final String SP_KEY = "BasePresenter.SP";
    private WeakReference<V> view = null;

    /**
     * Load has been called for the current {@link #view}.
     */
    private boolean loaded;

    public final void takeView(V view) {
        Timber.d("%s : takeView", getClass().getName());
        if (view == null) throw new NullPointerException("new view must not be null");

        if (this.view != null) dropView(this.view.get());

        this.view = new WeakReference<>(view);
        if (!loaded) {
            loaded = true;
            onLoad();
        }
    }

    public final void dropView(V view) {
        Timber.d("%s : dropView", getClass().getName());
        if (view == null) throw new NullPointerException("dropped view must not be null");
        loaded = false;
        this.view = null;
        onDestroy();
    }

    protected final V getView() {
        if (view == null) throw new NullPointerException("getView calle" +
                "d when view is null. Ensure takeView(View view) is called first.");
        return view.get();
    }

    protected final boolean checkView(){
        return view != null;
    }

    protected void onLoad() {
        Timber.d("%s : onLoad", getClass().getName());
    }

    protected void onDestroy() {
        Timber.d("%s : onDestroy", getClass().getName());
    }

    protected void onRestore(@NonNull Bundle savedInstanceState) {
        Timber.d("%s : onRestore", getClass().getName());
    }

    protected void onSave(@NonNull Bundle outState) {
        Timber.d("%s : onSave", getClass().getName());
    }


}
