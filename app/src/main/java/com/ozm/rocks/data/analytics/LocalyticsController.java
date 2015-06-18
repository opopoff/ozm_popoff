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

    /**
     * Events from doc: https://docs.google.com/document/d/1o3pFrn8gTIwUGJMOlesNTs1mttXQy77qYpVxXTMMe7c/edit
     */

    private static final String OPEN_APP            = "OPEN_APP";
    private static final String OPEN_FEED           = "OPEN_FEED";
    private static final String LIBRARY_FILTER      = "LIBRARY_FILTER";
    private static final String SAW_N_PICS_IN_FEED  = "SAW_N_PICS_IN_FEED";
    private static final String LIKE                = "LIKE";
    private static final String SEND_PICS           = "SEND_PICS";
    private static final String MESSENGER_ICON_TAP  = "MESSENGER_ICON_TAP";
    private static final String OPEN_LIBRARY        = "OPEN_LIBRARY";
    private static final String OPEN_FOLDER         = "OPEN_FOLDER";
    private static final String OPEN_FAVORITES      = "OPEN_FAVORITES";
    private static final String OPEN_SETTINGS       = "OPEN_SETTINGS";
    private static final String WIDGET_SETTINGS     = "WIDGET_SETTINGS";
    private static final String TAP_TO_TOP          = "TAP_TO_TOP";
    private static final String TAP_TO_SAVE         = "TAP_TO_SAVE";
    private static final String SAVE_ONBOARDING     = "SAVE_ONBOARDING";
    private static final String TOP_ONBOARDING      = "TOP_ONBOARDING";
    private static final String WIDGET_ONBOARDING   = "WIDGET_ONBOARDING";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ WIDGET, URL, DIRECT })
    public @interface OpenAppPlace {
    }
    public static final String WIDGET = "WIDGET";
    public static final String URL    = "URL";
    public static final String DIRECT = "DIRECT";

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

    /**
     * OPEN_APP - срабатывает при открытии окна приложения
     * (даже если оно просто было свернуто);
     * @param from - источник открытия (widget, url, direct);
     */
    public void openApp(@OpenAppPlace String from) {
        Timber.d("Localitycs: OPEN_APP = %s", from);
        Map<String, String> values = new HashMap<String, String>();
        values.put(OPEN_APP, from);
        Localytics.tagEvent(OPEN_APP, values);
    }

    /**
     * OPEN_FEED - открытие ленты;
     * @param type - способ открытия ленты (ICON, WIZARD);
     */
    public void openFeed(@OpenFeedType String type) {
        Timber.d("Localitycs: OPEN_FEED = %s", type);
        Map<String, String> values = new HashMap<String, String>();
        values.put(OPEN_FEED, type);
        Localytics.tagEvent(OPEN_FEED, values);
    }

    /**
     * LIBRARY_FILTER - тап по фильтру вверху ленты и ее сортировка.
     * Не срабатывает когда фильтр включается автоматом при переходе через свежак;
     * @param filterName - тематика фильтра;
     */
    public void openFilter(String filterName) {
        Timber.d("Localitycs: LIBRARY_FILTER = %s", filterName);
        Map<String, String> values = new HashMap<String, String>();
        values.put(LIBRARY_FILTER, filterName);
        Localytics.tagEvent(LIBRARY_FILTER, values);
    }

    /**
     * SAW_N_PICS_IN_FEED - Ивент срабатывает когда пользователь за сессию
     * в ленте посмотрел хотя бы N картинок\гифок.
     * @param decide - N может принимать
     * значения 10, 20, 30, … При каждом запуске приложения
     * счетчик обнуляется. Если сработал ивент на 50, значит до этого должны были
     * сработать ивенты на 40, 30, 20, 10. То есть пропускать ивенты не нужно.
     * Название ивента всегда остается SAW_N_PICS_IN_FEED (а не SAW_20_PICS_IN_FEED, например).
     */
    public void showedNImages(int decide) {
        Timber.d("Localitycs: SAW_N_PICS_IN_FEED = %d", decide);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SAW_N_PICS_IN_FEED, String.valueOf(decide));
        Localytics.tagEvent(SAW_N_PICS_IN_FEED, values);
    }

    /**
     * LIKE - срабатывает на каждый лайк;
     * @param imageType - тип файла (гиф или картинка);
     */
    public void like(@ImageType String imageType) {
        Timber.d("Localitycs: LIKE = %s", imageType);
        Map<String, String> values = new HashMap<String, String>();
        values.put(LIKE, imageType);
        Localytics.tagEvent(LIKE, values);
    }

    /**
     * SEND_PICS - срабатывает на каждую отправку;
     * @param sharePlace - место отправки (FEED\LIBRARY\FAVORITES);
     */
    public void share(@SharePlace String sharePlace) {
        Timber.d("Localitycs: SEND_PICS = %s", sharePlace);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SEND_PICS, sharePlace);
        Localytics.tagEvent(SEND_PICS, values);
    }

    /**
     * MESSENGER_ICON_TAP - срабатывает когда пользователь тапает
     * по иконки любого мессенджера или соцсети. * В параметры передается
     * @param applicationName - название мессенджера или соцсети (whatsapp,
     * viber, vk, fb, hangouts, skype, * telegramm, ok, moimir);
     */
    public void shareOutside(String applicationName) {
        Timber.d("Localitycs: MESSENGER_ICON_TAP = %s", applicationName);
        Map<String, String> values = new HashMap<String, String>();
        values.put(MESSENGER_ICON_TAP, applicationName);
        Localytics.tagEvent(MESSENGER_ICON_TAP, values);
    }

    /**
     * OPEN_LIBRARY - открытие библиотеки эмоций;
     */
    public void openCategories() {
        Timber.d("Localitycs: OPEN_LIBRARY");
        Localytics.tagEvent(OPEN_LIBRARY);
    }

    /**
     * OPEN_FAVORITES - открытие избранного;
     */
    public void openFavorites() {
        Timber.d("Localitycs: OPEN_FAVORITES");
        Localytics.tagEvent(OPEN_FAVORITES);
    }

    /**
     * OPEN_SETTINGS - открытие окна настроек;
     */
    public void openSettings() {
        Timber.d("Localitycs: OPEN_SETTINGS");
        Localytics.tagEvent(OPEN_SETTINGS);
    }

    /**
     * WIDGET_SETTINGS - срабатывает при переключении бегунка. ;
     * @param state - ON (включил виджет) и OFF (выключил);
     */
    public void setWidgetState(@WidgetState String state) {
        Timber.d("Localitycs: WIDGET_SETTINGS = %s", state);
        Map<String, String> values = new HashMap<String, String>();
        values.put(WIDGET_SETTINGS, state);
        Localytics.tagEvent(WIDGET_SETTINGS, values);
    }

    /**
     * OPEN_FOLDER - открытие золотой коллекции;
     * @param folderName - название папки;
     */
    public void openGoldenCollection(String folderName) {
        Timber.d("Localitycs: OPEN_FOLDER = %s", folderName);
        Map<String, String> values = new HashMap<String, String>();
        values.put(OPEN_FOLDER, folderName);
        Localytics.tagEvent(OPEN_FOLDER, values);
    }

    /**
     * TAP_TO_SAVE - срабатывает каждый раз, когда пользователь
     * нажимает на “сохранить” в папке спецпроекта;
     */
    public void pinGoldenCollection() {
        Timber.d("Localitycs: TAP_TO_SAVE");
        Localytics.tagEvent(TAP_TO_SAVE);
    }

    /**
     * TAP_TO_TOP - срабатывает каждый раз, когда пользователь
     * нажимает на кнопку “поднять наверх” внутри папки;
     */
    public void pickupGoldenCollection() {
        Timber.d("Localitycs: TAP_TO_TOP");
        Localytics.tagEvent(TAP_TO_TOP);
    }

    /**
     * TOP_ONBOARDING - срабатывает при появлении типсы про поднятие папки в топ;
     */
    public void showProptPinGoldenCollection() {
        Timber.d("Localitycs: TOP_ONBOARDING");
        Localytics.tagEvent(TOP_ONBOARDING);
    }

    /**
     * SWEAR_ONBOARDING - срабатывает при появлении типсы про мат в настройках;
     */
    public void showProptPickupGoldenCollection() {
        Timber.d("Localitycs: SAVE_ONBOARDING");
        Localytics.tagEvent(SAVE_ONBOARDING);
    }

    /**
     * WIDGET_ONBOARDING - срабатывает при открытии онборинга с анимацией,
     * рассказывающей про виджет и ссылку под картинкой;
     * @param page - В параметры  передается SCREEN1
     * (показали первый экран = вообще срабатыванию онбординга)
     * и SCREEN2 (показали второй экран онбординга);
     */
    public void showOnBoardingPage(int page) {
        Timber.d("Localitycs: WIDGET_ONBOARDING = %d", page);
        Map<String, String> values = new HashMap<String, String>();
        values.put(WIDGET_ONBOARDING, "SCREEN" + String.valueOf(page));
        Localytics.tagEvent(WIDGET_ONBOARDING, values);
    }

}
