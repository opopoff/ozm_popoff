package com.ozm.rocks.ui.widget;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.ozm.R;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.ui.start.LoadingActivity;

import javax.inject.Inject;

@ApplicationScope
public class WidgetController {

    private static final int NOTIFICATION_ID = 1;

    private final Context context;
    private final TokenStorage tokenStorage;
    private final NotificationManager notificationManager;

    @Inject
    public WidgetController(Application application, TokenStorage tokenStorage) {
        this.context = application.getApplicationContext();
        this.tokenStorage = tokenStorage;
        this.notificationManager = (NotificationManager) context.getSystemService(
                Application.NOTIFICATION_SERVICE);
    }

    public void start() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setColor(context.getResources().getColor(R.color.primary))
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_launcher))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentTitle(context.getString(R.string.widget_title))
                        .setContentText(context.getString(R.string.widget_context));

        // Create start activity intent;
        Intent intent = new Intent(context, LoadingActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        mBuilder.setContentIntent(resultPendingIntent);

        // Show notification;
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void stop() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void checkOnRunning() {
        final boolean isShowWidget = tokenStorage.isShowWidget();
        if (isShowWidget) {
            start();
        } else {
            stop();
        }
    }
}
