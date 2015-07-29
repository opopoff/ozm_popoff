package com.ozm.rocks.data.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public final class RestConfig {
    @SerializedName("sharingInformationEnabled")
    public final boolean sharingInformationEnabled;
    @SerializedName("replyUrl")
    public final String replyUrl;
    @SerializedName("replyUrlText")
    public final String replyUrlText;
    @SerializedName("messengerConfigs")
    public final List<MessengerConfigs> messengerConfigs;
    @SerializedName("messengerOrder")
    public final List<MessengerOrder> messengerOrders;
    @SerializedName("gifMessengerOrder")
    public final List<GifMessengerOrder> gifMessengerOrders;
    @SerializedName("obsceneDisabled")
    public final Boolean obsceneDisabled;
    @SerializedName("localyticsSegment")
    public final String localyticsSegment;
    @SerializedName("pushwooshTags")
    public final HashMap<String, Object> pushwooshTags;

    public RestConfig(boolean sharingInformationEnabled, String replyUrl, String replyUrlText,
                      List<MessengerConfigs> messengerConfigs, List<MessengerOrder> messengerOrders,
                      List<GifMessengerOrder> gifMessengerOrders, Boolean obsceneDisabled,
                      String localyticsSegment, HashMap<String, Object> pushwooshTags) {
        this.sharingInformationEnabled = sharingInformationEnabled;
        this.replyUrl = replyUrl;
        this.replyUrlText = replyUrlText;
        this.messengerConfigs = messengerConfigs;
        this.messengerOrders = messengerOrders;
        this.gifMessengerOrders = gifMessengerOrders;
        this.obsceneDisabled = obsceneDisabled;
        this.localyticsSegment = localyticsSegment;
        this.pushwooshTags = pushwooshTags;
    }

}
