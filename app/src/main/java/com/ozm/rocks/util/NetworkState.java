package com.ozm.rocks.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import timber.log.Timber;

public class NetworkState {

    private Context context;
    private NetworkStateReceiver mNetworkStateReceiver;
    private IConnected listener;

    public NetworkState(Context context) {
        this.context = context;
        mNetworkStateReceiver = new NetworkStateReceiver();
    }

    public void setConnectedListener(IConnected iConnected) {
        this.listener = iConnected;
    }

    public class NetworkStateReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =
                    connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (listener != null) {
                    listener.connectedState(true);
                }
            } else {
                if (listener != null) {
                    listener.connectedState(false);
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
        }
    }

    public interface IConnected {
        void connectedState(boolean isConnected);
    }
}
