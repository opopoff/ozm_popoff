package com.ozm.rocks.data.analytics;

import android.support.annotation.StringDef;

import com.localytics.android.Localytics;
import com.ozm.rocks.ui.ApplicationScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

@ApplicationScope
public class LocalyticsController {

    private static final String OPEN_FEED           = "OPEN_FEED";
    private static final String LIBRARY_FILTER      = "LIBRARY_FILTER";
    private static final String SAW_N_PICS_IN_FEED  = "SAW_N_PICS_IN_FEED";
    private static final String LIKE                = "LIKE";
    private static final String SEND_PICS           = "SEND_PICS";
    private static final String MESSENGER_ICON_TAP  = "MESSENGER_ICON_TAP";
    private static final String OPEN_LIBRARY        = "OPEN_LIBRARY";
    private static final String OPEN_FOLDER         = "OPEN_FOLDER";
    private static final String OPEN_APP            = "OPEN_APP";
    private static final String OPEN_FAVORITES      = "OPEN_FAVORITES";
    private static final String OPEN_SETTINGS       = "OPEN_SETTINGS";
    private static final String WIDGET_SETTINGS     = "WIDGET_SETTINGS";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ ICON, WIZARD , TAB })
    public @interface OpenFeedType {
    }
    public static final String ICON   = "ICON";
    public static final String WIZARD = "WIZARD";
    public static final String TAB    = "TAB";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ JPEG, GIF })
    public @interface ImageType {
    }
    public static final String JPEG = "JPEG";
    public static final String GIF  = "GIF";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ FEED, LIBRARY, FAVORITES })
    public @interface SharePlace {
    }
    public static final String FEED      = "FEED";
    public static final String LIBRARY   = "LIBRARY";
    public static final String FAVORITES = "FAVORITES";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ ON, OFF })
    public @interface WidgetState {
    }
    public static final String ON   = "ON";
    public static final String OFF  = "OFF";

    @Inject
    public LocalyticsController() {
        // nothing;
    }

    public void openFeed(@OpenFeedType String type) {
        Timber.d("Localitycs: OPEN_FEED = %s", type);
        Map<String, String> values = new HashMap<String, String>();
        values.put(OPEN_FEED, type);
        Localytics.tagEvent(OPEN_FEED, values);
    }

    public void openFilter(String filterName) {
        Timber.d("Localitycs: LIBRARY_FILTER = %s", filterName);
        Map<String, String> values = new HashMap<String, String>();
        values.put(LIBRARY_FILTER, filterName);
        Localytics.tagEvent(LIBRARY_FILTER, values);
    }

    public void showedNImages(int decide) {
        Timber.d("Localitycs: SAW_N_PICS_IN_FEED = %d", decide);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SAW_N_PICS_IN_FEED, String.valueOf(decide));
        Localytics.tagEvent(SAW_N_PICS_IN_FEED, values);
    }

    public void like(@ImageType String imageType) {
        Timber.d("Localitycs: LIKE = %s", imageType);
        Map<String, String> values = new HashMap<String, String>();
        values.put(LIKE, imageType);
        Localytics.tagEvent(LIKE, values);
    }

    public void share(@SharePlace String sharePlace) {
        Timber.d("Localitycs: SEND_PICS = %s", sharePlace);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SEND_PICS, sharePlace);
        Localytics.tagEvent(SEND_PICS, values);
    }

    public void shareOutside(String applicationName) {
        Timber.d("Localitycs: MESSENGER_ICON_TAP = %s", applicationName);
        Map<String, String> values = new HashMap<String, String>();
        values.put(MESSENGER_ICON_TAP, applicationName);
        Localytics.tagEvent(MESSENGER_ICON_TAP, values);
    }

    public void openCategories() {
        Timber.d("Localitycs: OPEN_LIBRARY");
        Localytics.tagEvent(OPEN_LIBRARY);
    }

    public void openFavorites() {
        Timber.d("Localitycs: OPEN_FAVORITES");
        Localytics.tagEvent(OPEN_FAVORITES);
    }

    public void openSettings() {
        Timber.d("Localitycs: OPEN_SETTINGS");
        Localytics.tagEvent(OPEN_SETTINGS);
    }

    public void setWidgetState(@WidgetState String state) {
        Timber.d("Localitycs: WIDGET_SETTINGS = %s", state);
        Map<String, String> values = new HashMap<String, String>();
        values.put(WIDGET_SETTINGS, state);
        Localytics.tagEvent(WIDGET_SETTINGS, values);
    }

    public void openGoldenCollection(String folderName) {
        Timber.d("Localitycs: OPEN_FOLDER = %s", folderName);
        Map<String, String> values = new HashMap<String, String>();
        values.put(OPEN_FOLDER, folderName);
        Localytics.tagEvent(OPEN_FOLDER, values);
    }

}
