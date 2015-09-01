package com.ozm.fun;

import android.app.Application;

import com.koushikdutta.ion.Ion;
import com.ozm.fun.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.fun.base.tools.KeyboardPresenter;
import com.ozm.fun.base.tools.ToastPresenter;
import com.ozm.fun.data.Clock;
import com.ozm.fun.data.DataService;
import com.ozm.fun.data.FileService;
import com.ozm.fun.data.SharingService;
import com.ozm.fun.data.TokenStorage;
import com.ozm.fun.data.analytics.LocalyticsController;
import com.ozm.fun.data.image.OzomeImageLoader;
import com.ozm.fun.data.prefs.rating.RatingStorage;
import com.ozm.fun.data.social.SocialPresenter;
import com.ozm.fun.ui.ActivityHierarchyServer;
import com.ozm.fun.ui.AppContainer;
import com.ozm.fun.ui.ApplicationSwitcher;
import com.ozm.fun.ui.OnGoBackPresenter;
import com.ozm.fun.ui.message.NoInternetPresenter;
import com.ozm.fun.ui.screen.main.SendFriendDialogBuilder;
import com.ozm.fun.ui.screen.main.personal.OnBoardingDialogBuilder;
import com.ozm.fun.ui.screen.sharing.choose.dialog.ChooseDialogBuilder;
import com.ozm.fun.ui.widget.WidgetController;
import com.ozm.fun.util.NetworkState;
import com.ozm.fun.util.PackageManagerTools;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;


/**
 * A common interface implemented by both the Release and Debug flavored components.
 */
public interface OzomeDependencies {
    void inject(OzomeApplication app);

    Application application();

    AppContainer appContainer();

//    RefWatcher refWatcher();

    OzomeImageLoader ozomeImageLoader();

    Picasso picasso();

    Ion ion();

    OkHttpClient okHttpClient();

    ActivityScreenSwitcher activityScreenSwitcher();

    ActivityHierarchyServer activityHierarchyServer();

    DataService dataService();

    TokenStorage tokenStorage();

    RatingStorage ratingStorage();

    Clock clock();

    KeyboardPresenter keyboardPresenter();

    ToastPresenter toastPresenter();

    PackageManagerTools packageManagerTools();

    ChooseDialogBuilder chooseDialogBuilder();

    SendFriendDialogBuilder sendFriendDialogBuilder();

    FileService fileService();

    NetworkState networkState();

    SharingService sharingService();

    NoInternetPresenter noInternetPresenter();

    WidgetController widgetController();

    OnGoBackPresenter onBackPresenter();

    LocalyticsController localyticsController();

    SocialPresenter vkpresenter();

    ApplicationSwitcher applicationSwitcher();

    OnBoardingDialogBuilder onBoardingDialogBuilder();
}
