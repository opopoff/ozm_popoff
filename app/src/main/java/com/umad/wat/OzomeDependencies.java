package com.umad.wat;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.umad.wat.base.navigation.activity.ActivityScreenSwitcher;
import com.umad.wat.base.tools.KeyboardPresenter;
import com.umad.wat.base.tools.ToastPresenter;
import com.umad.wat.data.Clock;
import com.umad.wat.data.DataService;
import com.umad.wat.data.FileService;
import com.umad.wat.data.SharingService;
import com.umad.wat.data.TokenStorage;
import com.umad.wat.data.analytics.LocalyticsController;
import com.umad.wat.data.image.OzomeImageLoader;
import com.umad.wat.data.prefs.rating.RatingStorage;
import com.umad.wat.data.social.SocialPresenter;
import com.umad.wat.ui.ActivityHierarchyServer;
import com.umad.wat.ui.AppContainer;
import com.umad.wat.ui.ApplicationSwitcher;
import com.umad.wat.ui.OnGoBackPresenter;
import com.umad.wat.ui.message.NoInternetPresenter;
import com.umad.wat.ui.screen.main.SendFriendDialogBuilder;
import com.umad.wat.ui.screen.main.personal.OnBoardingDialogBuilder;
import com.umad.wat.ui.screen.sharing.choose.dialog.ChooseDialogBuilder;
import com.umad.wat.ui.widget.WidgetController;
import com.umad.wat.util.NetworkState;
import com.umad.wat.util.PackageManagerTools;


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
