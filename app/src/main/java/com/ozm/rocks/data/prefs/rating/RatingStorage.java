package com.ozm.rocks.data.prefs.rating;

import android.support.annotation.IntDef;

import com.ozm.rocks.ApplicationScope;
import com.ozm.rocks.data.prefs.BooleanPreference;
import com.ozm.rocks.data.prefs.IntPreference;
import com.ozm.rocks.data.prefs.LongPreference;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

@ApplicationScope
public class RatingStorage {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOT_SHOWED, SHOWED, IGNORED, NOT_RATED})
    public @interface RatingStatus {
    }

    public static final int NOT_SHOWED = 0;
    public static final int SHOWED = 1;
    public static final int IGNORED = 2;
    public static final int NOT_RATED = 3;

    public static final long ONE_DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
    public static final long FIRST_SHOW_DELAY = 2 * ONE_DAY_IN_MILLISECONDS;
    public static final long IGNORE_SHOW_DELAY = 7 * ONE_DAY_IN_MILLISECONDS;
    public static final long NOT_RATED_SHOW_DELAT = 2 * 30 * ONE_DAY_IN_MILLISECONDS;

    private final LongPreference timerPreference;
    private final IntPreference statusPreference;
    private final IntPreference lastVersionPreference;
    private final BooleanPreference showRatingDebugPreference;

    @Inject
    RatingStorage(@TimerQualifier LongPreference timerPreference,
                  @StatusQualifier IntPreference statusPreference,
                  @LastVersionQualifier IntPreference lastVersionPreference,
                  @DebugShowQualifier BooleanPreference showRatingDebugPreference) {

        this.timerPreference = timerPreference;
        this.statusPreference = statusPreference;
        this.lastVersionPreference = lastVersionPreference;
        this.showRatingDebugPreference = showRatingDebugPreference;
    }

    public long getTimer() {
        return timerPreference.get();
    }

    public void setTimer(long timer) {
        timerPreference.set(timer);
    }

    public int getStatus() {
        return statusPreference.get();
    }

    public void setStatus(@RatingStatus int status) {
        statusPreference.set(status);
    }

    public int getLastVersion() {
        return lastVersionPreference.get();
    }

    public void setLastVersion(int lastVersion) {
        lastVersionPreference.set(lastVersion);
    }

    public boolean getShowRatingDebug() {
        return showRatingDebugPreference.get();
    }

    public void setShowRatingDebug(boolean showRatingDebug) {
        showRatingDebugPreference.set(showRatingDebug);
    }
}
