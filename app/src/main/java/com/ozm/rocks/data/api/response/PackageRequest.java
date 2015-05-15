package com.ozm.rocks.data.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Danil on 15.05.2015.
 */
public class PackageRequest {
    @SerializedName("messengers")
    public final List<Messenger> messengerList;

    public PackageRequest(List<Messenger> messengerList) {
        this.messengerList = messengerList;
    }

    public static PackageRequest create(List<Messenger> messengerList) {
        return new PackageRequest(messengerList);
    }
}
