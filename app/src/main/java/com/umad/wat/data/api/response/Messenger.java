package com.umad.wat.data.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Danil on 15.05.2015.
 */
public class Messenger {
    @SerializedName("applicationId")
    public final String applicationId;

    public Messenger(String applicationId) {
        this.applicationId = applicationId;
    }

    public static Messenger create(String applicationId) {
        return new Messenger(applicationId);
    }
}
