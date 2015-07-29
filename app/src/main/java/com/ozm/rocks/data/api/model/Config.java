package com.ozm.rocks.data.api.model;

import android.os.Parcelable;

import com.ozm.rocks.data.api.response.GifMessengerOrder;
import com.ozm.rocks.data.api.response.MessengerConfigs;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.data.api.response.PushwooshTags;
import com.ozm.rocks.data.api.response.RestConfig;

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

    public abstract String localyticsSegment();

    public abstract HashMap<String, Object> pushwooshTags();

    public static Config create(boolean sharingInformationEnabled, String replyUrl, String replyUrlText,
                                List<MessengerConfigs> messengerConfigs, List<MessengerOrder> messengerOrders,
                                List<GifMessengerOrder> gifMessengerOrders, boolean obsceneDisabled, String from,
                                String localyticsSegment, HashMap<String, Object> pushwooshTags) {
        return new AutoParcel_Config(sharingInformationEnabled, replyUrl, replyUrlText, messengerConfigs,
                messengerOrders, gifMessengerOrders, obsceneDisabled, from, localyticsSegment, pushwooshTags);
    }

    public static Config from(RestConfig restConfig, String from) {
        final boolean sharingInformationEnabled = restConfig.sharingInformationEnabled;
        final String replyUrl = restConfig.replyUrl;
        final String replyUrlText = restConfig.replyUrlText;
        final List<MessengerConfigs> messengerConfigs = restConfig.messengerConfigs;
        final List<MessengerOrder> messengerOrders = restConfig.messengerOrders;
        final List<GifMessengerOrder> gifMessengerOrders = restConfig.gifMessengerOrders;
        final Boolean obsceneDisabled = restConfig.obsceneDisabled;
        final String localyticsSegment = restConfig.localyticsSegment;
        final HashMap<String, Object> pushwooshTags = restConfig.pushwooshTags;
        return Config.create(sharingInformationEnabled, replyUrl, replyUrlText, messengerConfigs,
                messengerOrders, gifMessengerOrders, obsceneDisabled == null ? false : obsceneDisabled,
                from, localyticsSegment, pushwooshTags);
    }
}
