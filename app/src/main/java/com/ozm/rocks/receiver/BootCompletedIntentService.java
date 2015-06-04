package com.ozm.rocks.receiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ozm.rocks.OzomeApplication;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.ui.widget.WidgetService;

import javax.inject.Inject;

public class BootCompletedIntentService extends Service {

    @Inject
    TokenStorage tokenStorage;

    private BootCompletedIntentComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        OzomeApplication app = OzomeApplication.get(this);
        component = DaggerBootCompletedIntentComponent.builder().
                bootCompletedIntentModule(new BootCompletedIntentModule()).
                ozomeComponent(app.component()).build();
        component.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final boolean isShowWidget = tokenStorage.isShowWidget();
        final boolean isWidgetStarted = WidgetService.isServiceRunning(this);
        if (isShowWidget && !isWidgetStarted) {
            startService(new Intent(BootCompletedIntentService.this, WidgetService.class));
        }
        stopSelf(startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
