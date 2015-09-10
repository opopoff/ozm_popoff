package com.umad.wat.data.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Danil on 14.05.2015.
 */
public class MessengerConfigs {
    @SerializedName("applicationId")
    public final String applicationId;
    @SerializedName("supportsGIF")
    public final boolean supportsGIF;
    @SerializedName("supportsImageReply")
    public final boolean supportsImageReply;
    @SerializedName("supportsImageTextReply")
    public final boolean supportsImageTextReply;
    @SerializedName("supportsTextReply")
    public final boolean supportsTextReply;
    @SerializedName("supportsVideo")
    public final boolean supportsVideo;


    public MessengerConfigs(String applicationId, boolean supportsGIF, boolean supportsImageReply,
                            boolean supportsImageTextReply, boolean supportsTextReply, boolean supportsVideo) {
        this.applicationId = applicationId;
        this.supportsGIF = supportsGIF;
        this.supportsImageReply = supportsImageReply;
        this.supportsImageTextReply = supportsImageTextReply;
        this.supportsTextReply = supportsTextReply;
        this.supportsVideo = supportsVideo;
    }
}
