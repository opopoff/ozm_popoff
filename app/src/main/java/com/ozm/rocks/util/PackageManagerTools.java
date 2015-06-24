package com.ozm.rocks.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageManagerTools {
    private final Context mApplication;
    public static final String FB_MESSENGER_PACKAGE = "com.facebook.orca";
    public static final String VK_PACKAGE = "com.vkontakte.android";

    public PackageManagerTools(Application application) {
        this.mApplication = application;
    }

    public ArrayList<PInfo> getInstalledPackages() {
        ArrayList<PInfo> res = new ArrayList<>();
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfo = mApplication.getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo p : resInfo) {
                PInfo newInfo = new PInfo();
                final ApplicationInfo applicationInfo = p.activityInfo.applicationInfo;
                newInfo.setApplicationName(applicationInfo.loadLabel(mApplication.getPackageManager()).toString());
                newInfo.setPackageName(applicationInfo.packageName);
                newInfo.setIcon(applicationInfo.loadIcon(mApplication.getPackageManager()));
                res.add(newInfo);

            }
        }
        // Remove dublicate objects;
        Set<PInfo> set = new HashSet<PInfo>(res);
        return new ArrayList<PInfo>(set);
    }
}
