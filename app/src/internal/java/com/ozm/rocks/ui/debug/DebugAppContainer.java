package com.ozm.rocks.ui.debug;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.madge.MadgeFrameLayout;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.mattprecious.telescope.TelescopeLayout;
import com.ozm.R;
import com.ozm.rocks.data.LumberYard;
import com.ozm.rocks.data.PixelGridEnabled;
import com.ozm.rocks.data.PixelRatioEnabled;
import com.ozm.rocks.data.ScalpelEnabled;
import com.ozm.rocks.data.ScalpelWireframeEnabled;
import com.ozm.rocks.ui.ActivityHierarchyServer;
import com.ozm.rocks.ui.AppContainer;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.ui.bugreport.BugReportLens;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.FULL_WAKE_LOCK;
import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

@ApplicationScope
public final class DebugAppContainer implements AppContainer {
    private final Observable<Boolean> pixelGridEnabled;
    private final Observable<Boolean> pixelRatioEnabled;
    private final Observable<Boolean> scalpelEnabled;
    private final Observable<Boolean> scalpelWireframeEnabled;
    private final LumberYard lumberYard;

    static class ViewHolder {
        @InjectView(R.id.debug_drawer_layout)
        DrawerLayout drawerLayout;
        @InjectView(R.id.debug_drawer)
        ViewGroup debugDrawer;
        @InjectView(R.id.madge_container)
        MadgeFrameLayout madgeFrameLayout;
        @InjectView(R.id.debug_content)
        ScalpelFrameLayout content;
    }

    @Inject
    public DebugAppContainer(@PixelGridEnabled Observable<Boolean> pixelGridEnabled,
                             @PixelRatioEnabled Observable<Boolean> pixelRatioEnabled,
                             @ScalpelEnabled Observable<Boolean> scalpelEnabled,
                             @ScalpelWireframeEnabled Observable<Boolean> scalpelWireframeEnabled,
                             LumberYard lumberYard) {
        this.pixelGridEnabled = pixelGridEnabled;
        this.pixelRatioEnabled = pixelRatioEnabled;
        this.scalpelEnabled = scalpelEnabled;
        this.scalpelWireframeEnabled = scalpelWireframeEnabled;
        this.lumberYard = lumberYard;
    }

    @Override
    public ViewGroup get(final Activity activity) {
        activity.setContentView(R.layout.debug_activity_frame);
        final ViewHolder viewHolder = new ViewHolder();
        ButterKnife.inject(viewHolder, activity);

        final Context drawerContext = new ContextThemeWrapper(activity, R.style.Theme_U2020_Debug);
        final DebugView debugView = new DebugView(drawerContext);
        viewHolder.debugDrawer.addView(debugView);

        // Set up the contextual actions to watch views coming in and out of the content area.
        ContextualDebugActions contextualActions = debugView.getContextualDebugActions();
        contextualActions.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.drawerLayout.closeDrawers();
            }
        });
        viewHolder.content.setOnHierarchyChangeListener(HierarchyTreeChangeListener.wrap(contextualActions));

        viewHolder.drawerLayout.setDrawerShadow(R.drawable.debug_drawer_shadow, Gravity.END);
        viewHolder.drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                debugView.onDrawerOpened();
            }
        });

        final CompositeSubscription subscriptions = new CompositeSubscription();
        setupMadge(viewHolder, subscriptions);
        setupScalpel(viewHolder, subscriptions);

        final Application app = activity.getApplication();
        app.registerActivityLifecycleCallbacks(new ActivityHierarchyServer.Empty() {
            @Override
            public void onActivityDestroyed(Activity lifecycleActivity) {
                if (lifecycleActivity == activity) {
                    subscriptions.unsubscribe();
                    app.unregisterActivityLifecycleCallbacks(this);
                }
            }
        });

        riseAndShine(activity);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            final LayoutInflater layoutInflater = activity.getLayoutInflater();
            ViewGroup telescopeLayout = (ViewGroup) layoutInflater.inflate(R.layout.internal_activity_frame, null);
            TelescopeLayout.cleanUp(activity); // Clean up any old screenshots.
            ((TelescopeLayout) telescopeLayout).setLens(new BugReportLens(activity, lumberYard));
            final ViewGroup parent = (ViewGroup) viewHolder.drawerLayout.getParent();
            parent.removeView(viewHolder.drawerLayout);
            parent.addView(telescopeLayout);
            telescopeLayout.addView(viewHolder.drawerLayout);
        }

        return viewHolder.content;
    }

    private void setupMadge(final ViewHolder viewHolder, CompositeSubscription subscriptions) {
        subscriptions.add(pixelGridEnabled.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean enabled) {
                viewHolder.madgeFrameLayout.setOverlayEnabled(enabled);
            }
        }));
        subscriptions.add(pixelRatioEnabled.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean enabled) {
                viewHolder.madgeFrameLayout.setOverlayRatioEnabled(enabled);
            }
        }));
    }

    private void setupScalpel(final ViewHolder viewHolder, CompositeSubscription subscriptions) {
        subscriptions.add(scalpelEnabled.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean enabled) {
                viewHolder.content.setLayerInteractionEnabled(enabled);
            }
        }));
        subscriptions.add(scalpelWireframeEnabled.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean enabled) {
                viewHolder.content.setDrawViews(!enabled);
            }
        }));
    }

    /**
     * Show the activity over the lock-screen and wake up the device. If you launched the app manually
     * both of these conditions are already true. If you deployed from the IDE, however, this will
     * save you from hundreds of power button presses and pattern swiping per day!
     */
    public static void riseAndShine(Activity activity) {
        activity.getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED);

        PowerManager power = (PowerManager) activity.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock lock =
                power.newWakeLock(FULL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE, "wakeup!");
        lock.acquire();
        lock.release();
    }
}
