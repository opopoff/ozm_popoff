package com.ozm.fun.util;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ozm.fun.ApplicationScope;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import timber.log.Timber;

@ApplicationScope
public class NetworkState {

    private Context context;
    private NetworkStateReceiver mNetworkStateReceiver;
    private ConcurrentMap<String, IConnected> listeners;

    private boolean lastState = false;

    private int bindCounter;

    @Inject
    public NetworkState(Application application) {
        this.context = application.getApplicationContext();
        mNetworkStateReceiver = new NetworkStateReceiver();
        listeners = new ConcurrentHashMap<>();
    }

    public void addConnectedListener(String keyListener, IConnected iConnected) {
        this.listeners.put(keyListener, iConnected);
    }

    public void deleteConnectedListener(String keyListener) {
        this.listeners.remove(keyListener);
    }

    private void postEvents() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        final boolean isConnect = networkInfo != null && networkInfo.isConnected();
        Timber.i("NetworkStateReceiver: catch event isConnect = %b", isConnect);
        if (isConnect == lastState)  {
            return;
        }
        Timber.i("NetworkStateReceiver: change state = %b", isConnect);
        lastState = isConnect;
        if (listeners != null && listeners.size() > 0) {
            Iterator<Map.Entry<String, IConnected>> it = listeners.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, IConnected> entry = it.next();
                listeners.get(entry.getKey()).connectedState(isConnect);
            }
        }
    }

    public void bind() {
        bindCounter++;
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        context.registerReceiver(mNetworkStateReceiver, intentFilter);
//        LocalBroadcastManager.getInstance(context).registerReceiver(mNetworkStateReceiver, intentFilter);
        Timber.i("NetworkStateReceiver: bind");
    }

    public void unbind() {
        bindCounter--;
        Timber.i("NetworkStateReceiver: try unbinding");
        if (bindCounter > 0) return;
//        LocalBroadcastManager.getInstance(context).unregisterReceiver(mNetworkStateReceiver);
        context.unregisterReceiver(mNetworkStateReceiver);
        Timber.i("NetworkStateReceiver: unbind");
    }

    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean hasWifiConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public interface IConnected {
        void connectedState(boolean isConnected);
    }

    public class NetworkStateReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            postEvents();
        }
    }
}
