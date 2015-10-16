package com.umad.wat.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;

import com.umad.wat.ApplicationScope;
import com.umad.wat.data.model.PInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

@ApplicationScope
public class PackageManagerTools {
    private final Context context;

    @Inject
    public PackageManagerTools(Application application) {
        this.context = application.getApplicationContext();
    }

    public ArrayList<PInfo> getInstalledPackages() {
        ArrayList<PInfo> res = new ArrayList<>();
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo p : resInfo) {
                PInfo newInfo = new PInfo();
                final ApplicationInfo applicationInfo = p.activityInfo.applicationInfo;
                newInfo.setApplicationName(applicationInfo.loadLabel(context.getPackageManager()).toString());
                newInfo.setPackageName(applicationInfo.packageName);
                newInfo.setIcon(((BitmapDrawable) applicationInfo.loadIcon(
                        context.getPackageManager())).getBitmap());
                res.add(newInfo);

            }
        }
        // Remove dublicate objects;
        Set<PInfo> set = new HashSet<PInfo>(res);
        return new ArrayList<PInfo>(set);
    }

    //Для того, чтобы отправлять адкватное название приложения в локалитикс,
    // так как на разных языках названия приложений могут отличатся
    public static enum Messanger {
        FACEBOOK_MESSANGER("com.facebook.orca", "Messanger"),
        VKONTAKTE("com.vkontakte.android", "VKontakte"),
        TELEGRAM("org.telegram.messenger", "Telegram"),
        VIBER("com.viber.voip", "Viber"),
        WHATSAPP("com.whatsapp", "WhatsApp"),
        OK("ru.ok.android", "OK"),
        SKYPE("com.skype.raider", "Skype"),
        HANGOUT("com.google.android.talk", "Hangout");

        private final String packagename;
        private final String appname;

        Messanger(String packagename, String appname) {
            this.packagename = packagename;
            this.appname = appname;
        }

        public String getPackagename() {
            return packagename;
        }

        public String getAppname() {
            return appname;
        }
    }
}
