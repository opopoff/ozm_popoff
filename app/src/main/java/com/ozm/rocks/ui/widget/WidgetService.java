package com.ozm.rocks.ui.widget;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ozm.rocks.ui.screen.main.MainActivity;
import com.ozm.rocks.ui.screen.start.StartActivity;

import java.util.List;

public class WidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wakeUpApplication(this);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void wakeUpApplication(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(100);
        if (taskInfo.size() > 0) {
            boolean isRunning = false;
            for (ActivityManager.RunningTaskInfo runningTaskInfo : taskInfo) {
                final String shortClassName = runningTaskInfo.topActivity.getShortClassName();
                if (shortClassName.contains("com.ozm.rocks")) {
                    isRunning = true;
                    break;
                }
            }
            if (!isRunning) {
                Intent intentActivity = new Intent(context, StartActivity.class);
                intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentActivity.putExtra(StartActivity.WP_OPEN_FROM_WIDGET, "success");
                context.startActivity(intentActivity);
            } else {
                Intent intentActivity = new Intent(context, MainActivity.class);
//                intentActivity.setAction(Intent.ACTION_MAIN);
//                intentActivity.addCategory(Intent.CATEGORY_LAUNCHER);
                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intentActivity.putExtra(MainActivity.WP_OPEN_FROM_WIDGET, "success");
                context.startActivity(intentActivity);
            }
        }
    }
}
