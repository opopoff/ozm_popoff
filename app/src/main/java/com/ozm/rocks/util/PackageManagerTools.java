package com.ozm.rocks.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;

import java.util.ArrayList;
import java.util.List;

public class PackageManagerTools {
    private final Context mApplication;

    public PackageManagerTools(Application application) {
        this.mApplication = application;
    }

    public ArrayList<PInfo> getInstalledPackages() {
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        return apps;
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<>();
        List<PackageInfo> packs = mApplication.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            PInfo newInfo = new PInfo();
            newInfo.setAppname(p.applicationInfo.loadLabel(mApplication.getPackageManager()).toString());
            newInfo.setPname(p.packageName);
            newInfo.setVersionName(p.versionName);
            newInfo.setVersionCode(p.versionCode);
            newInfo.setIcon(p.applicationInfo.loadIcon(mApplication.getPackageManager()));
            res.add(newInfo);
        }
        return res;
    }
}
