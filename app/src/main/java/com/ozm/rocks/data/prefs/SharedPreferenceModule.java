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

    private static final String SP_ON_BOARDING = "GeneralPresenter.SP.OnBoarding";
    private static final String SP_USER_KEY = "SharedPreferenceModule.user.key";
    private static final String SP_USER_SECRET = "SharedPreferenceModule.user.secret";
    private static final String SP_SHOW_WIDGET = "SharedPreferenceModule.show.widget";

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
    @OnBoardingQualifier
    BooleanPreference provideOnBoardingQualifier(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, SP_ON_BOARDING, true);
    }
}
