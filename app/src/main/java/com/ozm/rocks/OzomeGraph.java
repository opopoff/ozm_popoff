package com.ozm.rocks;

import android.app.Application;

import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.Clock;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.ui.ActivityHierarchyServer;
import com.ozm.rocks.ui.AppContainer;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PackageManagerTools;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;


/**
 * A common interface implemented by both the Release and Debug flavored components.
 */
public interface OzomeGraph {
    void inject(OzomeApplication app);

    Application application();

    AppContainer appContainer();

    Picasso picasso();

    OkHttpClient okHttpClient();

    ActivityScreenSwitcher activityScreenSwitcher();

    ActivityHierarchyServer activityHierarchyServer();

    DataService dataService();

    TokenStorage tokenStorage();

    Clock clock();

    KeyboardPresenter keyboardPresenter();

    PackageManagerTools packageManagerTools();

    SharingDialogBuilder sharingDialogBuilder();

    NetworkState networkState();
}
