package com.umad.wat.data.api.model;

import android.os.Parcelable;

import com.google.gson.Gson;
import com.umad.wat.data.api.response.GifMessengerOrder;
import com.umad.wat.data.api.response.MessengerConfigs;
import com.umad.wat.data.api.response.MessengerOrder;
import com.umad.wat.data.api.response.RestConfig;
import com.umad.wat.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Config implements Parcelable {

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

    public static Config merge(RestConfig restConfig, String configString, String from) {
        final boolean sharingInformationEnabled = restConfig.sharingInformationEnabled;
        final String replyUrl = restConfig.replyUrl;
        final String replyUrlText = restConfig.replyUrlText;
        final List<MessengerConfigs> messengerConfigs = restConfig.messengerConfigs;
        List<MessengerOrder> messengerOrders;
        List<GifMessengerOrder> gifMessengerOrders;
        if (!Strings.isBlank(configString)) {
            Gson gson = new Gson();
            RestConfig temp = gson.fromJson(configString, RestConfig.class);
            messengerOrders = temp.messengerOrders;
            gifMessengerOrders = temp.gifMessengerOrders;
        } else {
            messengerOrders = new ArrayList<>(messengerConfigs.size());
            gifMessengerOrders = new ArrayList<>(messengerConfigs.size());
            for (MessengerConfigs configs : messengerConfigs) {
                messengerOrders.add(new MessengerOrder(configs.applicationId));
                gifMessengerOrders.add(new GifMessengerOrder(configs.applicationId));
            }
//            messengerOrders = restConfig.messengerOrders;
//            gifMessengerOrders = restConfig.gifMessengerOrders;
        }
        final Boolean obsceneDisabled = restConfig.obsceneDisabled;
        final List<String> localyticsSegments = restConfig.localyticsSegments == null
                ? new ArrayList<String>() : restConfig.localyticsSegments;
        final HashMap<String, Object> pushwooshTags = restConfig.pushwooshTags == null
                ? new HashMap<String, Object>() : restConfig.pushwooshTags;
        return Config.create(sharingInformationEnabled, replyUrl, replyUrlText, messengerConfigs,
                messengerOrders, gifMessengerOrders, obsceneDisabled == null ? false : obsceneDisabled,
                from, localyticsSegments, pushwooshTags);
    }

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

    public void upMessenger(String appId, boolean isGif) {
        synchronized (this) {
            if (isGif) {
                for (int i = 0; i < gifMessengerOrders().size(); i++) {
                    GifMessengerOrder temp = gifMessengerOrders().get(i);
                    if (temp.applicationId.equals(appId)) {
                        gifMessengerOrders().remove(i);
                        gifMessengerOrders().add(0, temp);
                    }
                }
            } else {
                for (int i = 0; i < messengerOrders().size(); i++) {
                    MessengerOrder temp = messengerOrders().get(i);
                    if (temp.applicationId.equals(appId)) {
                        messengerOrders().remove(i);
                        messengerOrders().add(0, temp);
                    }
                }
            }
        }
    }

    public RestConfig toRest() {
        return new RestConfig(
                sharingInformationEnabled(),
                replyUrl(),
                replyUrlText(),
                messengerConfigs(),
                messengerOrders(),
                gifMessengerOrders(),
                obsceneDisabled(),
                localyticsSegments(),
                pushwooshTags()
        );
    }
}