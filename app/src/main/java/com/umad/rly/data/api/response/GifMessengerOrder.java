package com.umad.rly.data.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Danil on 15.05.2015.
 */
public class GifMessengerOrder {
    @SerializedName("applicationId")
    public final String applicationId;

    public GifMessengerOrder(String applicationId) {
        this.applicationId = applicationId;
    }

    public static GifMessengerOrder create(String applicationId) {
        return new GifMessengerOrder(applicationId);
    }
}
