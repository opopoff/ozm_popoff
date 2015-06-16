package com.ozm.rocks.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class DeviceManagerTools {
    private static final String STRING_COLON = ":";
    private static final String STRING_EMPTY = "";
    private static final int DECIDE_SHIFT = 32;

    private DeviceManagerTools() {
        // nothing;
    }

    public static String getUniqueDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice = STRING_EMPTY + tm.getDeviceId();
        final String tmSerial = STRING_EMPTY + tm.getSimSerialNumber();
        String andrId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        final String androidId = STRING_EMPTY + andrId;

        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << DECIDE_SHIFT) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }

    public static String getMacAddressWithoutColon(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        return macAddress.replace(STRING_COLON, STRING_EMPTY);
    }

}
