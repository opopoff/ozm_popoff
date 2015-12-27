package com.umad.wat.data.analytics;

import android.support.annotation.StringDef;

import com.localytics.android.Localytics;
import com.umad.wat.ApplicationScope;
import com.umad.wat.data.TokenStorage;
import com.umad.wat.util.Timestamp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

@ApplicationScope
public class LocalyticsController {

    /**
     * Events from doc: https://docs.google.com/document/d/1S2-DpWW1FPFvnZYy-NtNxeYkU_sEhXD3AjVaiSALb2c/edit
     */

    private static final String OPEN_APP = "OPEN_APP";
    private static final String OPEN_FEED = "OPEN_FEED";
    private static final String LIBRARY_FILTER = "LIBRARY_FILTER";
    private static final String SAW_N_PICS_IN_FEED = "SAW_N_PICS_IN_FEED";
    private static final String SAW_N_PICS_IN_NEW = "SAW_N_PICS_IN_NEW";
    private static final String LIKE = "LIKE";
    private static final String SHARE = "SHARE";
    private static final String SEND_PICS = "SEND_PICS";
    private static final String SEND_X_PICS = "SEND_X_PICS";
    private static final String MESSENGER_ICON_TAP = "MESSENGER_ICON_TAP";
    private static final String OPEN_FOLDER = "OPEN_FOLDER";
    private static final String OPEN_FAVORITES = "OPEN_FAVORITES";
    private static final String OPEN_SETTINGS = "OPEN_SETTINGS";
    private static final String WIDGET_SETTINGS = "WIDGET_SETTINGS";
    private static final String TAP_TO_TOP = "TAP_TO_TOP";
    private static final String TAP_TO_SAVE = "TAP_TO_SAVE";
    private static final String SAVE_ONBOARDING = "SAVE_ONBOARDING";
    private static final String TOP_ONBOARDING = "TOP_ONBOARDING";
    private static final String WIDGET_ONBOARDING = "WIDGET_ONBOARDING";
    private static final String ALBUM_ONBOARDING = "ALBUM_ONBOARDING";
    private static final String OPEN_NEW = "OPEN_NEW";
    private static final String OPEN_HISTORY = "OPEN_HISTORY";
    private static final String OPEN_BEST = "OPEN_BEST";
    private static final String ALBUM_SETTINGS = "ALBUM_SETTINGS";
    private static final String SWEAR_SETTING = "SWEAR_SETTING";
    private static final String OPEN_APP_X_TIME = "OPEN_APP_X_TIME";
    private static final String VK_AUTHORIZATION_START = "VK_AUTHORIZATION_START";
    private static final String VK_AUTHORIZATION = "VK_AUTHORIZATION";
    private static final String SHARE_OZM = "SHARE_OZM";
    private static final String SPLASHSCREEN_SHOW = "SPLASHSCREEN_SHOW";
    private static final String MEDUZA = "MEDUZA";
    private static final String ASSIGN_SEGMENT = "ASSIGN_SEGMENT";


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({WIDGET, URL, DIRECT})
    public @interface OpenAppPlace {
    }

    public static final String WIDGET = "WIDGET";
    public static final String URL = "URL";
    public static final String DIRECT = "DIRECT";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ICON, WIZARD, TAB})
    public @interface OpenFeedType {
    }

    public static final String ICON = "ICON";
    public static final String WIZARD = "WIZARD";
    public static final String TAB = "TAB";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({JPEG, GIF})
    public @interface ImageType {
    }

    public static final String JPEG = "JPEG";
    public static final String GIF = "GIF";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({NEW, HISTORY, FAVORITES})
    public @interface SharePlace {
    }

    public static final String NEW = "NEW";
    public static final String HISTORY = "HISTORY";
    public static final String FAVORITES = "FAVORITES";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ON, OFF})
    public @interface WidgetState {
    }

    public static final String ON = "ON";
    public static final String OFF = "OFF";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({CREATE, SKIP})
    public @interface OnboardingAction {
    }

    public static final String CREATE = "CREATE";
    public static final String SKIP = "SKIP";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({SUCCESS, CANCEL, START})
    public @interface SocialAuthorizationEvent {
    }

    public static final String SUCCESS = "SUCCESS";
    public static final String CANCEL = "CANCEL";
    public static final String START = "START";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({SIDEBAR, SPLASHSCREEN, VK})
    public @interface ShareOzmEvent {
    }

    public static final String SIDEBAR = "SIDEBAR";
    public static final String SPLASHSCREEN = "SPLASHSCREEN";
    public static final String VK = "VK";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({SHOW, FIRST_YES, FIRST_NO, SECOND_YES, SECOND_NO})
    public @interface MeduzaEvent {
    }

    public static final String SHOW = "SHOW";
    public static final String FIRST_YES = "FIRST_YES";
    public static final String FIRST_NO = "FIRST_NO";
    public static final String SECOND_YES = "SECOND_YES";
    public static final String SECOND_NO = "SECOND_NO ";

    private TokenStorage tokenStorage;

    private long startAppTime = 0;

    @Inject
    public LocalyticsController(TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
    }

    /**
     * OPEN_APP - срабатывает при открытии окна приложения
     * (даже если оно просто было свернуто);
     *
     * @param from - источник открытия (widget, url, direct);
     */
    public void openApp(@OpenAppPlace String from) {
        final long now = Timestamp.getUTC();
        if (now - startAppTime < 1) {
            return;
        }
        startAppTime = now;
        /**
         * Дело в том, что поймать событие разворачивания приложения из backgound можно только через
         * Application.ActivityLifecycleCallbacks. Т.о. разделить события поднятия приложения, певогово запуска,
         * открытия по notification или deeplink невозможно. Удалось сделать вызов events для открытия по
         * notification и deeplink разделать. Эти события всегда прихоядт раньше, чем событие старта от
         * Application.ActivityLifecycleCallbacks. Поэтому мы создали флаг с интервалом срабатывания в 1 секунду,
         * что бы отбрасывать лишний по условиям задачи event прямого запуска приложения при открытии приложения
         * по notification или deeplink;
         */
        Timber.d("Localitycs: OPEN_APP = %s", from);
        Map<String, String> values = new HashMap<String, String>();
        values.put(OPEN_APP, from);
        Localytics.tagEvent(OPEN_APP, values);
    }

    /**
     * OPEN_APP_X_TIME - срабатывает при каждом открытии и в параметр передается число раз.
     * Перестает срабатывать после 30. При обновлении приложения счетчик не сбрасывается;
     */
    public void openAppXTime() {
        final int startAppCounter = tokenStorage.getWakeUpAppXCounter() + 1;
        tokenStorage.setWakeUpAppXCounter(startAppCounter);
        if (startAppCounter > 30) {
            Timber.d("Localitycs: skip event for OPEN_APP_X_TIME = %d", startAppCounter);
            return;
        }
        Timber.d("Localitycs: OPEN_APP_X_TIME = %d", startAppCounter);
        Map<String, String> values = new HashMap<String, String>();
        values.put(OPEN_APP_X_TIME, String.valueOf(startAppCounter));
        Localytics.tagEvent(OPEN_APP_X_TIME, values);
    }

    /**
     * OPEN_FEED - открытие ленты;
     *
     * @param type - способ открытия ленты (ICON, WIZARD);
     */
    public void openFeed(@OpenFeedType String type) {
//        Timber.d("Localitycs: OPEN_FEED = %s", type);
//        Map<String, String> values = new HashMap<String, String>();
//        values.put(OPEN_FEED, type);
//        Localytics.tagEvent(OPEN_FEED, values);
    }

    /**
     * OPEN_FEED - открытие ленты переписок;
     */
    public void openFeed() {
        Timber.d("Localitycs: OPEN_FEED");
        Localytics.tagEvent(OPEN_FEED);
    }

    /**
     * LIBRARY_FILTER - тап по фильтру вверху ленты и ее сортировка.
     * Не срабатывает когда фильтр включается автоматом при переходе через свежак;
     *
     * @param filterName - тематика фильтра;
     */
    public void openFilter(String filterName) {
//        Timber.d("Localitycs: LIBRARY_FILTER = %s", filterName);
//        Map<String, String> values = new HashMap<String, String>();
//        values.put(LIBRARY_FILTER, filterName);
//        Localytics.tagEvent(LIBRARY_FILTER, values);
    }

    /**
     * SAW_N_PICS_IN_FEED - Ивент срабатывает когда пользователь за сессию
     * в ленте посмотрел хотя бы N картинок\гифок.
     *
     * @param decide - N может принимать
     *               значения 10, 20, 30, … При каждом запуске приложения
     *               счетчик обнуляется. Если сработал ивент на 50, значит до этого должны были
     *               сработать ивенты на 40, 30, 20, 10. То есть пропускать ивенты не нужно.
     *               Название ивента всегда остается SAW_N_PICS_IN_FEED (а не SAW_20_PICS_IN_FEED, например).
     */
    public void showedNImagesInFeed(int decide) {
        Timber.d("Localitycs: SAW_N_PICS_IN_FEED = %d", decide);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SAW_N_PICS_IN_FEED, String.valueOf(decide));
        Localytics.tagEvent(SAW_N_PICS_IN_FEED, values);
    }

    /**
     * SAW_N_PICS_IN_NEW - Ивент срабатывает когда пользователь за сессию
     * в ленте посмотрел хотя бы N картинок\гифок. При каждом запуске приложения счетчик обнуляется.
     * Если пользователь открыл новое, значит он увидел один экран с картинками и ему в параметр сразу
     * передается число картинок на экране. Если начинает скролить вниз, то оно увеличивается
     *
     * @param categoryName - название категории;
     * @param decide       - N может принимать
     *                     значения 10, 20, 30, … При каждом запуске приложения
     *                     счетчик обнуляется. Если сработал ивент на 50, значит до этого должны были
     *                     сработать ивенты на 40, 30, 20, 10. То есть пропускать ивенты не нужно.
     *                     Название ивента всегда остается SAW_N_PICS_IN_FEED (а не SAW_20_PICS_IN_FEED, например).
     */
    public void showedNImagesInNew(String categoryName, int decide) {
//        Timber.d("Localitycs: SAW_N_PICS_IN_NEW: CATEGORY_NAME=%s, N=%d", categoryName, decide);
//        Map<String, String> values = new HashMap<String, String>();
//        values.put("CATEGORY_NAME", categoryName);
//        values.put("N", String.valueOf(decide));
//        Localytics.tagEvent(SAW_N_PICS_IN_NEW, values);
    }

    /**
     * LIKE - срабатывает на каждый лайк;
     *
     * @param imageType - тип файла (гиф или картинка);
     */
    public void like(@ImageType String imageType) {
        Timber.d("Localitycs: LIKE = %s", imageType);
        Map<String, String> values = new HashMap<String, String>();
        values.put(LIKE, imageType);
        Localytics.tagEvent(LIKE, values);
    }

    /**
     * SHARE - срабатывает когда пользователь тапает по иконки любого мессенджера
     * или соцсети в окне просмотра картинки.
     * В случае с авторизовавшимися пользователями ВК срабатывает на каждое нажатие по
     * иконке друга  (ЭТО ПРОСТО ПЕРЕИМЕНОВАНИЕ ICON MESSANGER CLICK);
     *
     * @param applicationName - название мессенджера
     *                        или соцсети (whatsapp, viber, vk, fb, hangouts, skype, telegramm, ok, moimir);
     */
    public void share(String applicationName) {
        Timber.d("Localitycs: SHARE = %s", applicationName);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SHARE, applicationName);
        Localytics.tagEvent(SHARE, values);
    }

    /**
     * SEND_PICS - срабатывает на каждую отправку;
     *
     * @param sharePlace - место отправки (FEED\LIBRARY\FAVORITES);
     */
    public void sendSharePlace(@SharePlace String sharePlace) {
        Timber.d("Localitycs: SEND_PICS = %s", sharePlace);

        final int sharePicCounter = tokenStorage.getSharePicCounter() + 1;

        Map<String, String> values = new HashMap<String, String>();
        values.put("FROM", sharePlace);
        if (sharePicCounter > 30) {
            Timber.d("Localitycs: skip event for SEND_PICS = %d", sharePicCounter);
        } else {
            values.put("COUNT", sharePicCounter + "");
            tokenStorage.setSharePicCounter(sharePicCounter);
        }
        Localytics.tagEvent(SEND_PICS, values);
    }

    /**
     * SEND_X_PICS - срабатывает на каждую отправку и параметром передается число отправок
     * (независимо от сессии). В качестве Х передается число отправок.
     * Событие перестает передаваться после значения 30;
     */
    public void sendXPics() {
//        final int sharePicCounter = tokenStorage.getSharePicCounter() + 1;
//        if (sharePicCounter > 30) {
//            Timber.d("Localitycs: skip event for SEND_X_PICS = %d", sharePicCounter);
//            return;
//        }
//        tokenStorage.setSharePicCounter(sharePicCounter);
//        Timber.d("Localitycs: SEND_X_PICS = %d", sharePicCounter);
//        Map<String, String> values = new HashMap<String, String>();
//        values.put(SEND_X_PICS, String.valueOf(sharePicCounter));
//        Localytics.tagEvent(SEND_X_PICS, values);
    }

    /**
     * MESSENGER_ICON_TAP - срабатывает когда пользователь тапает
     * по иконки любого мессенджера или соцсети. * В параметры передается
     *
     * @param applicationName - название мессенджера или соцсети (whatsapp,
     *                        viber, vk, fb, hangouts, skype, * telegramm, ok, moimir);
     */
    public void shareOutside(String applicationName) {
//        Timber.d("Localitycs: MESSENGER_ICON_TAP = %s", applicationName);
//        Map<String, String> values = new HashMap<String, String>();
//        values.put(MESSENGER_ICON_TAP, applicationName);
//        Localytics.tagEvent(MESSENGER_ICON_TAP, values);
    }

    /**
     * OPEN_LIBRARY - открытие библиотеки эмоций;
     */
    public void openBest() {
//        Timber.d("Localitycs: OPEN_BEST");
//        Localytics.tagEvent(OPEN_BEST);
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
     * OPEN_HISTORY - открытие вкладки история;
     */
    public void openHistory() {
        Timber.d("Localitycs: OPEN_HISTORY");
        Localytics.tagEvent(OPEN_HISTORY);
    }

    /**
     * OPEN_NEW - открытие вкладки новое внутри папки эмоции;
     *
     * @param folderName - название папки;
     */
    public void openNew(String folderName) {
        Timber.d("Localitycs: OPEN_NEW");
        Map<String, String> values = new HashMap<String, String>();
        values.put(OPEN_NEW, folderName);
        Localytics.tagEvent(OPEN_NEW, values);
    }

    /**
     * WIDGET_SETTINGS - срабатывает при переключении бегунка. ;
     *
     * @param state - ON (включил виджет) и OFF (выключил);
     */
    public void setWidgetState(@WidgetState String state) {
        Timber.d("Localitycs: WIDGET_SETTINGS = %s", state);
        Map<String, String> values = new HashMap<String, String>();
        values.put(WIDGET_SETTINGS, state);
        Localytics.tagEvent(WIDGET_SETTINGS, values);
    }

    /**
     * ALBUM_SETTINGS - срабатывает при переключении бегунка;
     *
     * @param state - ON (включил альбом) и OFF (выключил);
     */
    public void setAlbumSettings(@WidgetState String state) {
        Timber.d("Localitycs: ALBUM_SETTINGS = %s", state);
        Map<String, String> values = new HashMap<String, String>();
        values.put(ALBUM_SETTINGS, state);
        Localytics.tagEvent(ALBUM_SETTINGS, values);
    }

    /**
     * SWEAR_SETTING - срабатывает при переключении бегунка;
     *
     * @param state - ON (включил альбом) и OFF (выключил);
     */
    public void setSwearSettings(@WidgetState String state) {
        Timber.d("Localitycs: SWEAR_SETTING = %s", state);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SWEAR_SETTING, state);
        Localytics.tagEvent(SWEAR_SETTING, values);
    }

    /**
     * OPEN_FOLDER - открытие золотой коллекции;
     *
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
    public void showPromptPinGoldenCollection() {
        Timber.d("Localitycs: TOP_ONBOARDING");
        Localytics.tagEvent(TOP_ONBOARDING);
    }

    /**
     * SAVE_ONBOARDING - срабатывает при появлении типсы про сохранение спецпроекта;
     */
    public void showProptPickupGoldenCollection() {
//        Timber.d("Localitycs: SAVE_ONBOARDING");
//        Localytics.tagEvent(SAVE_ONBOARDING);
    }

    /**
     * ALBUM_ONBOARDING - срабатывает когда пользователь на онбординге
     * видит предложение о создании альбома с фото на его телефоне;
     *
     * @param action - CREATE (нажал создать) или SKIP (нажал пропустить);
     */
    public void showAlbumOnBoarding(@OnboardingAction String action) {
        Timber.d("Localitycs: ALBUM_ONBOARDING = %s", action);
        Map<String, String> values = new HashMap<String, String>();
        values.put(ALBUM_ONBOARDING, action);
        Localytics.tagEvent(ALBUM_ONBOARDING, values);
    }

    /**
     * WIDGET_ONBOARDING - срабатывает при открытии онборинга с анимацией,
     * рассказывающей про виджет и ссылку под картинкой;
     *
     * @param page - В параметры  передается SCREEN1
     *             (показали первый экран = вообще срабатыванию онбординга)
     *             и SCREEN2 (показали второй экран онбординга);
     */
    public void showOnBoardingPage(int page) {
//        Timber.d("Localitycs: WIDGET_ONBOARDING = %d", page);
//        Map<String, String> values = new HashMap<String, String>();
//        values.put(WIDGET_ONBOARDING, "SCREEN" + String.valueOf(page));
//        Localytics.tagEvent(WIDGET_ONBOARDING, values);
    }

    /**
     * VK_AUTHORIZATION - посылается при попытке пользователя авторизоваться во ВК.
     *
     * @param authorizationEvent - SUCCESS (атторизовался) / CANCEL (отказался продолжать авторизацию).
     *                           / START - начало авторизации
     */
    public void setVkAuthorization(@SocialAuthorizationEvent String authorizationEvent) {
        Timber.d("Localitycs: VK_AUTHORIZATION = %s", authorizationEvent);
        Map<String, String> values = new HashMap<String, String>();
        values.put(VK_AUTHORIZATION, authorizationEvent);
        Localytics.tagEvent(VK_AUTHORIZATION, values);
    }

    /**
     * SHARE_OZM - посылается при шаринге «рассказать другу про OZM!».
     *
     * @param shareOzmEvent - SIDEBAR (из боковой шторки) / SPLASHSCREEN (из всплывающего диалогового окна)
     *                      VK - при отправке в вк со ссылкой
     */
    public void setShareOzm(@ShareOzmEvent String shareOzmEvent) {
        Timber.d("Localitycs: SHARE_OZM = %s", shareOzmEvent);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SHARE_OZM, shareOzmEvent);
        Localytics.tagEvent(SHARE_OZM, values);
    }

    /**
     * SPLASHSCREEN_SHOW - посылается открытии диалога рассказать другу.
     */
    public void setSplashscreenShow() {
        Timber.d("Localitycs: SPLASHSCREEN_SHOW = %s", SHARE_OZM);
        Map<String, String> values = new HashMap<String, String>();
        values.put(SPLASHSCREEN_SHOW, SHARE_OZM);
        Localytics.tagEvent(SPLASHSCREEN_SHOW, values);
    }

    /**
     * MEDUZA - Посылается при работе с RatingView
     *
     * @param meduza - SHOW - при показе; FIRST_YES - при нажатии на да на первом экране;
     *               FIRST_NO - при нажатии нет на первом экране;
     *               SECOND_YES - при нажатии да на втором экране;
     *               SECOND_NO - при нажатии нет на втором экране.
     */
    public void setMeduza(@MeduzaEvent String meduza) {
        Timber.d("Localitycs: MEDUZA = %s", meduza);
        Map<String, String> values = new HashMap<String, String>();
        values.put(MEDUZA, meduza);
        Localytics.tagEvent(MEDUZA, values);
    }

//    /**
//     * ASSIGN_SEGMENT - Посылается при получении списка Segments с сервера через Config;
//     * @param segments - список параметров сегментов, получаемых с сервера;
//     */
//    public void setSegments(List<String> segments) {
//        Timber.d("Localitycs: ASSIGN_SEGMENT = %s", meduza);
//        Map<String, String> values = new HashMap<String, String>();
//        values.put(ASSIGN_SEGMENT, meduza);
//        Localytics.tagEvent(ASSIGN_SEGMENT, values);
//    }
}
