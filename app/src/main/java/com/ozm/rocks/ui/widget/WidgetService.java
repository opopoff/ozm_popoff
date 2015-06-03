package com.ozm.rocks.ui.widget;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.ozm.R;
import com.ozm.rocks.ui.start.LoadingActivity;

public class WidgetService extends Service {

    private static final int NOTIFICATION_ID = 1;

    private WidgetBinder binder = new WidgetBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotif();
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendNotif() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setColor(getResources().getColor(R.color.primary))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_launcher))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentTitle(getString(R.string.widget_title))
                        .setContentText(getString(R.string.widget_context));

        // Create start activity intent;
        Intent intent = new Intent(this, LoadingActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(resultPendingIntent);

        // Show notification;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void closeService() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public class WidgetBinder extends Binder {

        public WidgetService getServiceInstance() {
            return WidgetService.this;
        }

    }
}
