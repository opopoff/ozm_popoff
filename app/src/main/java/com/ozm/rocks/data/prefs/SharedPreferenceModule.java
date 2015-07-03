package com.ozm.rocks.data.prefs;

import android.app.Application;
import android.content.SharedPreferences;

import com.ozm.rocks.ui.ApplicationScope;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

@Module
public class SharedPreferenceModule {

    private static final String SP_NAME = "ozome";

    private static final String SP_FEED_PROPMPT = "SharedPreference.feed.prompt";
    private static final String SP_ON_BOARDING = "SharedPreference.onBoarding";
    private static final String SP_USER_KEY = "SharedPreferenceModule.user.key";
    private static final String SP_USER_SECRET = "SharedPreferenceModule.user.secret";
    private static final String SP_SHOW_WIDGET = "SharedPreferenceModule.show.widget";
    private static final String SP_GOLD_FOUR_ON_BOARDING_STARTS = "SharedPreferenceModule.on.boarding.gold.starts";
    private static final String SP_CREATE_ALBUM = "SharedPreferenceModule.create.album";
    private static final String SP_UP_FOLDER = "SharedPreferenceModule.up.folder";
    private static final String SP_PERSONAL_POPUP_SHOWED = "SharedPreferenceModule.personal.popup.showed";
    private static final String SP_START_APP_COUNTER = "SharedPreferenceModule.start.app.counter";
    private static final String SP_SHARE_PIC_COUNTER = "SharedPreferenceModule.share.pic.counter";
    private static final String SP_VK_USER_PROFILE = "SharedPreferenceModule.vk.user.profile";

    @Provides
    @ApplicationScope
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences(SP_NAME, MODE_PRIVATE);
    }

    @Provides
    @ApplicationScope
    @UserKeyQualifier
    StringPreference provideUserKeyQualifier(SharedPreferences sharedPreferences) {
        return new StringPreference(sharedPreferences, SP_USER_KEY);
    }

    @Provides
    @ApplicationScope
    @UserSecretQualifier
    StringPreference provideUserSecretQualifier(SharedPreferences sharedPreferences) {
        return new StringPreference(sharedPreferences, SP_USER_SECRET);
    }

    @Provides
    @ApplicationScope
    @ShowWidgetQualifier
    BooleanPreference provideShowWidgetQualifier(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, SP_SHOW_WIDGET, true);
    }

    @Provides
    @ApplicationScope
    @OnBoardingGoldFourLoadQualifier
    IntPreference provideGoldFourOnBoarding(SharedPreferences sharedPreferences) {
        return new IntPreference(sharedPreferences, SP_GOLD_FOUR_ON_BOARDING_STARTS, 0);
    }

    @Provides
    @ApplicationScope
    @UpFolderQualifier
    BooleanPreference provideUpFolder(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, SP_UP_FOLDER, false);
    }

    @Provides
    @ApplicationScope
    @FeedPromptQualifier
    BooleanPreference provideFeedPromptQualifier(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, SP_FEED_PROPMPT, false);
    }

    @Provides
    @ApplicationScope
    @OnBoardingQualifier
    BooleanPreference provideOnBoardingQualifier(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, SP_ON_BOARDING, false);
    }

    @Provides
    @ApplicationScope
    @CreateAlbumQualifier
    BooleanPreference provideCreateAlbum(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, SP_CREATE_ALBUM, false);
    }

    @Provides
    @ApplicationScope
    @PersonalPopupShowed
    BooleanPreference providePersonalPopupShowed(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, SP_PERSONAL_POPUP_SHOWED, false);
    }

    @Provides
    @ApplicationScope
    @StartApplicationCounterQualifier
    IntPreference provideStartAppCounterQualifier(SharedPreferences sharedPreferences) {
        return new IntPreference(sharedPreferences, SP_START_APP_COUNTER, 0);
    }

    @Provides
    @ApplicationScope
    @SharePicsCounterQualifier
    IntPreference provideSharePicCounterQualifier(SharedPreferences sharedPreferences) {
        return new IntPreference(sharedPreferences, SP_SHARE_PIC_COUNTER, 0);
    }

    @Provides
    @ApplicationScope
    @VkUserProfileQualifier
    StringPreference provideVkUserProfileQualifier(SharedPreferences sharedPreferences) {
        return new StringPreference(sharedPreferences, SP_VK_USER_PROFILE, null);
    }
}
