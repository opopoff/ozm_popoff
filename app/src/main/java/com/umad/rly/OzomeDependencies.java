package com.umad.rly;

import android.app.Application;

import com.koushikdutta.ion.Ion;
import com.umad.rly.base.navigation.activity.ActivityScreenSwitcher;
import com.umad.rly.base.tools.KeyboardPresenter;
import com.umad.rly.base.tools.ToastPresenter;
import com.umad.rly.data.Clock;
import com.umad.rly.data.DataService;
import com.umad.rly.data.FileService;
import com.umad.rly.data.SharingService;
import com.umad.rly.data.TokenStorage;
import com.umad.rly.data.analytics.LocalyticsController;
import com.umad.rly.data.image.OzomeImageLoader;
import com.umad.rly.data.prefs.rating.RatingStorage;
import com.umad.rly.data.social.SocialPresenter;
import com.umad.rly.ui.ActivityHierarchyServer;
import com.umad.rly.ui.AppContainer;
import com.umad.rly.ui.ApplicationSwitcher;
import com.umad.rly.ui.OnGoBackPresenter;
import com.umad.rly.ui.message.NoInternetPresenter;
import com.umad.rly.ui.screen.main.SendFriendDialogBuilder;
import com.umad.rly.ui.screen.main.personal.OnBoardingDialogBuilder;
import com.umad.rly.ui.screen.sharing.choose.dialog.ChooseDialogBuilder;
import com.umad.rly.ui.widget.WidgetController;
import com.umad.rly.util.NetworkState;
import com.umad.rly.util.PackageManagerTools;
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
