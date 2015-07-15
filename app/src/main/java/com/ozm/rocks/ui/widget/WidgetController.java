package com.ozm.rocks.ui.widget;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.ozm.R;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.ui.ApplicationScope;

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

        // Create start activity intent;
        Intent intent = new Intent(context, WidgetService.class);
        PendingIntent resultPendingIntent = PendingIntent.getService(context, 0, intent, 0);

//        final NotificationCompat.Action action = new NotificationCompat.Action(
//                0, context.getString(R.string.widget_all_pictures_action_title), resultPendingIntent);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout2);
        remoteViews.setOnClickPendingIntent(R.id.widget_button, resultPendingIntent);

//        RemoteViews remoteBigViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

//        final NotificationCompat.BigPictureStyle bigTextStyle = new NotificationCompat.BigPictureStyle()
//                .setSummaryText("Sometext, Sometext, Sometext, Sometext, Sometext, " +
//                        "Sometext, Sometext, Sometext, Sometext, Sometext, " +
//                        "Sometext, Sometext, Sometext, Sometext, Sometext, " +
//                        "Sometext, Sometext, Sometext, Sometext, Sometext, " +
//                        "Sometext, Sometext, Sometext, Sometext, Sometext, Sometext, ")
//                .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.mipmap.bg_splash))
//                .bigLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ozome_launcher));

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setColor(context.getResources().getColor(R.color.primary))
//                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
//                                R.drawable.ic_widget_big))
                        .setSmallIcon(R.drawable.widget_icon)
                        .setPriority(NotificationCompat.PRIORITY_MAX);
//                        .setVisibility(NotificationCompat.VISIBILITY_SECRET);
//                        .setContentTitle(context.getString(R.string.widget_title))
//                        .setContentText(context.getString(R.string.widget_context));
//                        .setContent(remoteViews);
//                        .setStyle(bigTextStyle)
//                        .addAction(action);

        mBuilder.setContentIntent(resultPendingIntent);

        // Show notification;
        final Notification build = mBuilder.build();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            build.bigContentView = remoteBigViews;
//        }
        build.contentView = remoteViews;
        notificationManager.notify(NOTIFICATION_ID, build);
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
