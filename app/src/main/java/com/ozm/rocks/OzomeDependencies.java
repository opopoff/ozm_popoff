package com.ozm.rocks;

import android.app.Application;

import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.base.tools.ToastPresenter;
import com.ozm.rocks.data.Clock;
import com.ozm.rocks.data.DataService;
import com.ozm.rocks.data.FileService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.vk.VkPresenter;
import com.ozm.rocks.ui.ActivityHierarchyServer;
import com.ozm.rocks.ui.AppContainer;
import com.ozm.rocks.ui.OnGoBackPresenter;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.message.NoInternetPresenter;
import com.ozm.rocks.ui.sharing.ChooseDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingDialogBuilder;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.ui.widget.WidgetController;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PackageManagerTools;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;


/**
 * A common interface implemented by both the Release and Debug flavored components.
 */
public interface OzomeDependencies {
    void inject(OzomeApplication app);

    Application application();

    AppContainer appContainer();

    RefWatcher refWatcher();

    Picasso picasso();

    OkHttpClient okHttpClient();

    ActivityScreenSwitcher activityScreenSwitcher();

    ActivityHierarchyServer activityHierarchyServer();

    DataService dataService();

    TokenStorage tokenStorage();

    Clock clock();

    KeyboardPresenter keyboardPresenter();

    ToastPresenter toastPresenter();

    PackageManagerTools packageManagerTools();

    SharingDialogBuilder sharingDialogBuilder();

    ChooseDialogBuilder chooseDialogBuilder();

    FileService fileService();

    NetworkState networkState();

    SharingService sharingService();

    LikeHideResult likeHideResult();

    NoInternetPresenter noInternetPresenter();

    WidgetController widgetController();

    OnGoBackPresenter onBackPresenter();

    LocalyticsController localyticsController();

    VkPresenter vkpresenter();
}
