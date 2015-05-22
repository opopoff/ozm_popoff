package com.ozm.rocks.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class NetworkState {

    private Context context;
    private NetworkStateReceiver mNetworkStateReceiver;
    private Map<String, IConnected> listeners;

    public NetworkState(Context context) {
        this.context = context;
        mNetworkStateReceiver = new NetworkStateReceiver();
        listeners = new HashMap<>();
    }

    public void addConnectedListener(String keyListener, IConnected iConnected) {
        this.listeners.put(keyListener, iConnected);
    }

    public void deleteConnectedListener(String keyListener) {
        this.listeners.remove(keyListener);
    }

    public class NetworkStateReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =
                    connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                if (listeners != null && listeners.size() > 0) {
                    for (IConnected iConnected : listeners.values()) {
                        iConnected.connectedState(true);
                    }
                }
            } else {
                if (listeners != null && listeners.size() > 0) {
                    for (IConnected iConnected : listeners.values()) {
                        iConnected.connectedState(false);
                    }
                }
            }
        }

    }


    public void bind() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        context.registerReceiver(mNetworkStateReceiver, intentFilter);
        Timber.i("bind");
    }

    public void unbind() {
        try {
            context.unregisterReceiver(mNetworkStateReceiver);
            Timber.i("unbind");
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }

    public interface IConnected {
        void connectedState(boolean isConnected);
    }
}
