package com.ozm.fun.data.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Danil on 15.05.2015.
 */
public class MessengerOrder {
    @SerializedName("applicationId")
    public final String applicationId;

    public MessengerOrder(String applicationId) {
        this.applicationId = applicationId;
    }

    public static MessengerOrder create(String applicationId) {
        return new MessengerOrder(applicationId);
    }
}
