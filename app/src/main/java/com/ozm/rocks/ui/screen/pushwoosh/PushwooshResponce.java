package com.ozm.rocks.ui.screen.pushwoosh;

import com.google.gson.annotations.SerializedName;

public class PushwooshResponce {
    @SerializedName("url")
    public final String backUrl;

    public PushwooshResponce(String backUrl) {
        this.backUrl = backUrl;
    }
}
