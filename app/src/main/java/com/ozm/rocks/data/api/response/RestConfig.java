package com.ozm.rocks.data.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Danil on 14.05.2015.
 */
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

    public RestConfig(boolean sharingInformationEnabled, String replyUrl, String replyUrlText,
                      List<MessengerConfigs> messengerConfigs, List<MessengerOrder> messengerOrders,
                      List<GifMessengerOrder> gifMessengerOrders) {
        this.sharingInformationEnabled = sharingInformationEnabled;
        this.replyUrl = replyUrl;
        this.replyUrlText = replyUrlText;
        this.messengerConfigs = messengerConfigs;
        this.messengerOrders = messengerOrders;
        this.gifMessengerOrders = gifMessengerOrders;
    }
}
