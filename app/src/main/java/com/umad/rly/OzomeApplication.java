package com.umad.rly;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.beta.Beta;
import com.localytics.android.Localytics;
import com.ozm.BuildConfig;
import com.umad.rly.base.lifecycle.Foreground;
import com.umad.rly.data.analytics.LocalyticsController;
import com.umad.rly.ui.ActivityHierarchyServer;
import com.umad.rly.util.DeviceManagerTools;

import javax.inject.Inject;

import cat.ppicas.customtypeface.CustomTypeface;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;

public class OzomeApplication extends Application {
    private OzomeComponent component;

    @Inject
    ActivityHierarchyServer activityHierarchyServer;

    @Inject
    LocalyticsController localyticsController;

//    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate();

//        CalligraphyConfig.initDefault(
//                new CalligraphyConfig.Builder().
//                        setDefaultFontPath("fonts/roboto_regular.ttf").
//                        setFontAttrId(R.attr.fontPath).
//                        build()
//        );

        CustomTypeface.getInstance().registerTypeface("regular", getAssets(), "fonts/roboto_regular.ttf");
        CustomTypeface.getInstance().registerTypeface("light", getAssets(), "fonts/roboto_light.ttf");
        CustomTypeface.getInstance().registerTypeface("medium", getAssets(), "fonts/roboto_medium.ttf");

        // Integration of Localytics;
        Localytics.integrate(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
//            refWatcher = RefWatcher.DISABLED;
//            refWatcher = LeakCanary.install(this);
        } else {
            Fabric.with(this, new Crashlytics());
            Fabric.with(this, new Beta(), new Crashlytics());
//            refWatcher = RefWatcher.DISABLED;
        }

//        JodaTimeAndroid.init(this);

        buildComponentAndInject();

        registerActivityLifecycleCallbacks(activityHierarchyServer);

        Foreground.init(this);
        Foreground.get().addListener(new Foreground.Listener() {
            @Override
            public void onBecameForeground() {
                localyticsController.openAppXTime();
                localyticsController.openApp(LocalyticsController.DIRECT);
            }

            @Override
            public void onBecameBackground() {
            }
        });
        Timber.d("DeviceId: %s", DeviceManagerTools.getUniqueDeviceId(this));
    }

    public void buildComponentAndInject() {
        component = OzomeComponent.Initializer.init(this);
        component.inject(this);
    }

    public OzomeComponent component() {
        return component;
    }

    public static OzomeApplication get(Context context) {
        return (OzomeApplication) context.getApplicationContext();
    }

//    public RefWatcher getRefWatcher() {
//        return refWatcher;
//    }

}
