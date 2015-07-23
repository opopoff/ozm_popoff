package com.ozm.rocks.data.prefs.rating;

import android.content.SharedPreferences;

import com.ozm.rocks.ApplicationScope;
import com.ozm.rocks.data.prefs.BooleanPreference;
import com.ozm.rocks.data.prefs.IntPreference;
import com.ozm.rocks.data.prefs.LongPreference;

import dagger.Module;
import dagger.Provides;

@Module
public class RatingPreferenceModule {
    private static final String SP_TIMER = "RatingPreference.timer";
    private static final String SP_STATUS = "RatingPreference.status";
    private static final String SP_LAST_VERSION = "RatingPreference.last.version";
    private static final String SP_DEBUG_SHOW = "RatingPreference.debug.show";

    @Provides
    @ApplicationScope
    @TimerQualifier
    LongPreference provideTimer(SharedPreferences sharedPreferences) {
        return new LongPreference(sharedPreferences, SP_TIMER);
    }

    @Provides
    @ApplicationScope
    @StatusQualifier
    IntPreference provideStatus(SharedPreferences sharedPreferences) {
        return new IntPreference(sharedPreferences, SP_STATUS);
    }

    @Provides
    @ApplicationScope
    @LastVersionQualifier
    IntPreference provideLastVersion(SharedPreferences sharedPreferences) {
        return new IntPreference(sharedPreferences, SP_LAST_VERSION);
    }

    @Provides
    @ApplicationScope
    @DebugShowQualifier
    BooleanPreference provideDebugShow(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, SP_DEBUG_SHOW);
    }
}
