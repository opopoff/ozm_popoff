package com.ozm.rocks.ui.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ozm.rocks.OzomeApplication;
import com.ozm.rocks.receiver.DaggerWidgetComponent;

import javax.inject.Inject;

public class WidgetService extends Service {

    @Inject
    WidgetController widgetController;

    private WidgetComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        OzomeApplication app = OzomeApplication.get(this);
        component = DaggerWidgetComponent.builder().
                bootCompletedIntentModule(new WidgetModule()).
                ozomeComponent(app.component()).build();
        component.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        widgetController.chechOnRunning();
        stopSelf(startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
