package com.ozm.rocks.ui.settings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.tools.ToastPresenter;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainScope;
import com.ozm.rocks.ui.widget.WidgetService;

import javax.inject.Inject;

@MainScope
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private final MainActivity activity;
    private final ToastPresenter toastPresenter;

    private boolean mBounded;
    private WidgetService mService;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            toastPresenter.show("Service disconnect!");
            mBounded = false;
            mService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            toastPresenter.show("Service connect!");
            mBounded = true;
            WidgetService.WidgetBinder mLocalBinder = (WidgetService.WidgetBinder) service;
            mService = mLocalBinder.getServiceInstance();
        }
    };

    @Inject
    public SettingsPresenter(ToastPresenter toastPresenter, MainActivity activity) {
        this.toastPresenter = toastPresenter;
        this.activity = activity;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        bindService();
    }

    @Override
    protected void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    public void bindService() {
        Intent mIntent = new Intent(activity, WidgetService.class);
        activity.bindService(mIntent, mConnection, Activity.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (mBounded) {
            activity.unbindService(mConnection);
            mBounded = false;
            mService = null;
        }
    }

    public void startService() {
        WidgetService.startService(activity);
        bindService();
    }

    public void stopService() {
        if (mService != null) {
            mService.closeService();
        }
        WidgetService.stopService(activity);
        unbindService();
    }
}
