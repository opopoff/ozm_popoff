package com.ozm.rocks.base.mvp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ozm.R;
import com.ozm.rocks.OzomeApplication;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.tools.ToastPresenter;
import com.ozm.rocks.data.analytics.LocalyticsActivity;
import com.ozm.rocks.ui.AppContainer;
import com.ozm.rocks.ui.OnGoBackPresenter;
import com.ozm.rocks.ui.screen.main.SendFriendDialogBuilder;
import com.ozm.rocks.ui.message.MessageInterface;
import com.ozm.rocks.ui.message.NoInternetPresenter;
import com.ozm.rocks.ui.message.NoInternetView;
import com.ozm.rocks.data.SharingService;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.Strings;

import javax.inject.Inject;

import cat.ppicas.customtypeface.CustomTypeface;
import cat.ppicas.customtypeface.CustomTypefaceFactory;

public abstract class BaseActivity extends LocalyticsActivity implements MessageInterface {

    @Inject
    AppContainer appContainer;

    @Inject
    NetworkState networkState;

    @Inject
    NoInternetPresenter noInternetPresenter;

    @Inject
    SharingService sharingService;

    @Inject
    OnGoBackPresenter onGoBackPresenter;


    @Inject
    SendFriendDialogBuilder sendFriendDialogBuilder;

    @Inject
    ToastPresenter toastPresenter;

    private NoInternetView noInternetView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            onExtractParams(params);
        }
        getLayoutInflater().setFactory(new CustomTypefaceFactory(this, CustomTypeface.getInstance()));
        super.onCreate(savedInstanceState);

        OzomeApplication app = OzomeApplication.get(this);
        onCreateComponent(app.component());
        if (appContainer == null) {
            throw new IllegalStateException("No injection happened. Add component.inject(this)"
                    + " in onCreateComponent() implementation.");
        }
        Registry.add(this, viewId(), presenter());
        final LayoutInflater layoutInflater = getLayoutInflater();
        ViewGroup container = appContainer.get(this);
        ViewGroup base = (ViewGroup) layoutInflater.inflate(R.layout.base_layout, container);
        noInternetView = (NoInternetView) base.findViewById(R.id.no_internet_view);
        ViewGroup my = (ViewGroup) layoutInflater.inflate(layoutId(), null);
        base.addView(my, 0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        networkState.bind();
        noInternetPresenter.attach(this);
        sharingService.attach(this);
        sendFriendDialogBuilder.attach(this);
        toastPresenter.attach(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        toastPresenter.detach(this);
        networkState.unbind();
        noInternetPresenter.detach(this);
        sharingService.detach(this);
        sendFriendDialogBuilder.detach(this);
    }

    @Override
    public void onBackPressed() {
        if (onGoBackPresenter.getOnGoBackInterface() != null) {
            onGoBackPresenter.getOnGoBackInterface().onBack();
        } else {
            super.onBackPressed();
        }
    }

    protected void onExtractParams(@NonNull Bundle params) {
    }

    @Override
    public NoInternetView getNoNoInternetView() {
        return noInternetView;
    }

    public String uniqueKey() {
        return Strings.EMPTY;
    }

    protected abstract void onCreateComponent(OzomeComponent component);

    @LayoutRes
    protected abstract int layoutId();

    protected abstract BasePresenter<? extends BaseView> presenter();

    @IdRes
    protected abstract int viewId();

}
