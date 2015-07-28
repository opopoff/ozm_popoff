package com.ozm.rocks.data.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PackageRequest {
    @SerializedName("messengers")
    public final List<Messenger> messengerList;
    public final VkData vkData;
    public final String pushwooshToken;

    public PackageRequest(List<Messenger> messengerList, VkData vkData, String pushwooshToken) {
        this.messengerList = messengerList;
        this.vkData = vkData;
        this.pushwooshToken = pushwooshToken;
    }

    public static PackageRequest create(List<Messenger> messengerList, VkData vkData, String pushwooshToken) {
        return new PackageRequest(messengerList, vkData, pushwooshToken);
    }

    public static class VkData {
        public final long id;
        public final String name;
        public final String surname;

        public VkData(long id, String name, String surname) {
            this.id = id;
            this.name = name;
            this.surname = surname;
        }
    }
}
