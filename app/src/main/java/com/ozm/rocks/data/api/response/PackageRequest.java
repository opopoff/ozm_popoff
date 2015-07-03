package com.ozm.rocks.data.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Danil on 15.05.2015.
 */
public class PackageRequest {
    @SerializedName("messengers")
    public final List<Messenger> messengerList;
    public final VkData vkData;

    public PackageRequest(List<Messenger> messengerList, VkData vkData) {
        this.messengerList = messengerList;
        this.vkData = vkData;
    }

    public static PackageRequest create(List<Messenger> messengerList, VkData vkData) {
        return new PackageRequest(messengerList, vkData);
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
