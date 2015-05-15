package com.ozm.rocks;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.beta.Beta;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.ozm.BuildConfig;
import com.ozm.R;
import com.ozm.rocks.ui.ActivityHierarchyServer;

import org.jraf.android.util.activitylifecyclecallbackscompat.ApplicationHelper;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class OzomeApplication extends Application {
    private OzomeComponent component;

    @Inject
    ActivityHierarchyServer activityHierarchyServer;

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder().
                        setDefaultFontPath("fonts/roboto_regular.ttf").
                        setFontAttrId(R.attr.fontPath).
                        build()
        );
//
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Fabric.with(this, new Beta());
        }

//        JodaTimeAndroid.init(this);

        buildComponentAndInject();

        ApplicationHelper.registerActivityLifecycleCallbacks(this, activityHierarchyServer);
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
}
