package com.ozm.rocks.data.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Danil on 14.05.2015.
 */
public final class RestConfig {
    @SerializedName("imageBoxParameter")
    public final String imageBoxParameter;
    @SerializedName("replyUrl")
    public final String replyUrl;
    @SerializedName("replyUrlText")
    public final String replyUrlText;
    @SerializedName("messengerConfigs")
    public final List<MessengerConfigs> messengerConfigs;

    public RestConfig(String imageBoxParameter, String replyUrl, String replyUrlText,
                      List<MessengerConfigs> messengerConfigs) {
        this.imageBoxParameter = imageBoxParameter;
        this.replyUrl = replyUrl;
        this.replyUrlText = replyUrlText;
        this.messengerConfigs = messengerConfigs;
    }
}
