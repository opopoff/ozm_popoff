package com.umad.wat.ui.pushwoosh;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.arellomobile.android.push.utils.PreferenceUtils;
import com.arellomobile.android.push.utils.notification.AbsNotificationFactory;
import com.arellomobile.android.push.utils.notification.PushData;
import com.umad.R;
import com.pushwoosh.support.v4.app.NotificationCompat;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import timber.log.Timber;

public class PushwooshNotificationFactory extends AbsNotificationFactory {
    @Override
    public Notification onGenerateNotification(PushData pushData) {
        final Context context = this.getContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(this.getContentFromHtml(pushData.getHeader()));
        builder.setContentText(this.getContentFromHtml(pushData.getMessage()));
        builder.setSmallIcon(pushData.getSmallIconResId());
        builder.setTicker(this.getContentFromHtml(pushData.getTicker()));
        builder.setWhen(System.currentTimeMillis());
//        if (pushData.getBitmap() != null) {
//            builder.setStyle(new NotificationCompat.BigPictureStyle()
//                    .bigPicture(pushData.getBitmap())
//                    .setSummaryText(this.getContentFromHtml(pushData.getMessage())));
//        } else {
//            builder.setStyle(new NotificationCompat.BigTextStyle()
//                    .bigText(this.getContentFromHtml(pushData.getMessage())));
//        }

        if (pushData.getIconBackgroundColor() != null) {
            builder.setColor(Color.parseColor(pushData.getIconBackgroundColor()));
        }

        if (null != pushData.getCustomIconBitmap()) {
            builder.setLargeIcon(pushData.getCustomIconBitmap());
        }

        Notification build = builder.build();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_pooshwoosh);
        PushwooshData pushwooshData = (PushwooshData) pushData;
        try {
            JSONObject jsonObject = new JSONObject(pushwooshData.getUserdata());
            Bitmap bitmap = Picasso.with(context).load(jsonObject.optString("url")).get();
            remoteViews.setImageViewBitmap(R.id.poosh_background, bitmap);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Timber.e(e.getMessage());
        }
        remoteViews.setTextViewText(R.id.poosh_text, this.getContentFromHtml(pushData.getMessage()));
        build.contentView = remoteViews;
        this.addLED(build, PreferenceUtils.getEnableLED(context), PreferenceUtils.getLEDColor(context));
        this.addSound(build, pushData.getSound());
        this.addVibration(build, pushData.getVibration());
        this.addCancel(build);
        return build;
    }

    @Override
    public void onSendDeliveryRequest(boolean b, PushData pushData) {

    }

    @Override
    public void onPushHandle(Activity activity) {

    }
}
