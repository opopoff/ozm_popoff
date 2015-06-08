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
import com.ozm.rocks.ui.message.MessageInterface;
import com.ozm.rocks.ui.message.NoInternetPresenter;
import com.ozm.rocks.ui.AppContainer;
import com.ozm.rocks.ui.message.NoInternetView;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.NetworkState;

import org.jraf.android.util.activitylifecyclecallbackscompat.app.LifecycleDispatchActionBarActivity;

import javax.inject.Inject;

//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends LifecycleDispatchActionBarActivity implements MessageInterface {

    private static final String KEY_LISTENER = "BaseActivity";

    @Inject
    AppContainer appContainer;

    @Inject
    NetworkState networkState;

    @Inject
    NoInternetPresenter noInternetPresenter;

    @Inject
    SharingService sharingService;

    private NoInternetView noInternetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            onExtractParams(params);
        }
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
        super.onStart();
        networkState.bind();
        noInternetPresenter.attach(this);
        sharingService.attach(this);
    }

    @Override
    protected void onStop() {
        networkState.unbind();
        noInternetPresenter.detach();
        sharingService.detach();
        super.onStop();
    }


    protected void onExtractParams(@NonNull Bundle params) {
        // default no implemetation
    }

    @Override
    public NoInternetView getNoNoInternetView() {
        return noInternetView;
    }

    /**
     * Calligraphy Init
     */
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    /**
     * Must be implemented by derived activities. Injection must be performed here.
     * Otherwise IllegalStateException will be thrown. Derived activity is
     * responsible to create and store it's component.
     *
     * @param component application level component
     */
    protected abstract void onCreateComponent(OzomeComponent component);

    @LayoutRes
    protected abstract int layoutId();

    protected abstract BasePresenter<? extends BaseView> presenter();

    @IdRes
    protected abstract int viewId();

}
