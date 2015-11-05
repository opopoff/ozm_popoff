package com.umad.wat;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.StringDef;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.beta.Beta;
import com.facebook.stetho.Stetho;
import com.localytics.android.Localytics;
import com.umad.BuildConfig;
import com.umad.wat.base.lifecycle.Foreground;
import com.umad.wat.data.TokenStorage;
import com.umad.wat.data.analytics.LocalyticsController;
import com.umad.wat.ui.ActivityHierarchyServer;
import com.umad.wat.util.DeviceManagerTools;
import com.vk.sdk.VKSdk;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import cat.ppicas.customtypeface.CustomTypeface;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class OzomeApplication extends Application {

    @Inject
    ActivityHierarchyServer activityHierarchyServer;

    @Inject
    LocalyticsController localyticsController;

    @Inject
    TokenStorage tokenStorage;

    private OzomeComponent component;

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

        CustomTypeface.getInstance().registerTypeface("regular", getAssets(), "fonts/roboto_regular.ttf");
        CustomTypeface.getInstance().registerTypeface("light", getAssets(), "fonts/roboto_light.ttf");
        CustomTypeface.getInstance().registerTypeface("medium", getAssets(), "fonts/roboto_medium.ttf");

        // Integration of Localytics;
        Localytics.integrate(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
            Stetho.initializeWithDefaults(this);
        } else {
            Fabric.with(this, new Crashlytics());
            Fabric.with(this, new Beta(), new Crashlytics());
        }

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
        //vk
        VKSdk.initialize(this);

        tokenStorage.startAppCounter();
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

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ DEBUG, INTERNAL, STAGE, RELEASE })
    public @interface BuildType {
    }
    public static final String DEBUG = "debug";
    public static final String STAGE = "stage";
    public static final String INTERNAL = "internal";
    public static final String RELEASE = "release";
}
