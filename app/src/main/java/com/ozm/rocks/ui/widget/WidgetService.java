package com.ozm.rocks.ui.widget;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.start.LoadingActivity;

import java.util.List;

public class WidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
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
//            final ComponentName topActivity = taskInfo.get(0).topActivity;
//            final String shortClassName = topActivity.getShortClassName();
//            final String name = MainActivity.class.getName();

            if (!isRunning) {
                Intent intentActivity = new Intent(this, LoadingActivity.class);
                intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentActivity);
            } else {
                Intent intentActivity = new Intent(this, MainActivity.class);
//                intentActivity.setAction(Intent.ACTION_MAIN);
//                intentActivity.addCategory(Intent.CATEGORY_LAUNCHER);
                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentActivity);
            }

//            if (!isRunning) {
//                Intent intentActivity = new Intent(this, LoadingActivity.class);
//                intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intentActivity);
//            } else if (!shortClassName.equals(name)) {
//                Intent intentActivity = new Intent(this, MainActivity.class);
////                intentActivity.setAction(Intent.ACTION_MAIN);
////                intentActivity.addCategory(Intent.CATEGORY_LAUNCHER);
//                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
////                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intentActivity);
//            } else {
//                Intent intentActivity = new Intent(this, MainActivity.class);
////                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
////                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intentActivity);
//            }
        }

        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
