package com.umad.rly.data.api.model;

import android.os.Parcelable;

import com.umad.rly.data.api.response.GifMessengerOrder;
import com.umad.rly.data.api.response.MessengerConfigs;
import com.umad.rly.data.api.response.MessengerOrder;
import com.umad.rly.data.api.response.RestConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Config implements Parcelable {

    public abstract boolean sharingInformationEnabled();

    public abstract String replyUrl();

    public abstract String replyUrlText();

    public abstract List<MessengerConfigs> messengerConfigs();

    public abstract List<MessengerOrder> messengerOrders();

    public abstract List<GifMessengerOrder> gifMessengerOrders();

    public abstract boolean obsceneDisabled();

    public abstract String from();

    public abstract List<String> localyticsSegments();

    public abstract HashMap<String, Object> pushwooshTags();

    public static Config create(boolean sharingInformationEnabled, String replyUrl, String replyUrlText,
                                List<MessengerConfigs> messengerConfigs, List<MessengerOrder> messengerOrders,
                                List<GifMessengerOrder> gifMessengerOrders, boolean obsceneDisabled, String from,
                                List<String> localyticsSegments, HashMap<String, Object> pushwooshTags) {
        return new AutoParcel_Config(sharingInformationEnabled, replyUrl, replyUrlText, messengerConfigs,
                messengerOrders, gifMessengerOrders, obsceneDisabled, from, localyticsSegments, pushwooshTags);
    }

    public static Config from(RestConfig restConfig, String from) {
        final boolean sharingInformationEnabled = restConfig.sharingInformationEnabled;
        final String replyUrl = restConfig.replyUrl;
        final String replyUrlText = restConfig.replyUrlText;
        final List<MessengerConfigs> messengerConfigs = restConfig.messengerConfigs;
        final List<MessengerOrder> messengerOrders = restConfig.messengerOrders;
        final List<GifMessengerOrder> gifMessengerOrders = restConfig.gifMessengerOrders;
        final Boolean obsceneDisabled = restConfig.obsceneDisabled;
        final List<String> localyticsSegments = restConfig.localyticsSegments == null
                ? new ArrayList<String>() : restConfig.localyticsSegments;
        final HashMap<String, Object> pushwooshTags = restConfig.pushwooshTags == null
                ? new HashMap<String, Object>() : restConfig.pushwooshTags;
        return Config.create(sharingInformationEnabled, replyUrl, replyUrlText, messengerConfigs,
                messengerOrders, gifMessengerOrders, obsceneDisabled == null ? false : obsceneDisabled,
                from, localyticsSegments, pushwooshTags);
    }
}
