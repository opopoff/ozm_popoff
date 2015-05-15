package com.ozm.rocks.data.api.model;

import com.google.gson.annotations.SerializedName;
import com.ozm.rocks.data.api.response.GifMessengerOrder;
import com.ozm.rocks.data.api.response.MessengerConfigs;
import com.ozm.rocks.data.api.response.MessengerOrder;
import com.ozm.rocks.data.api.response.RestConfig;

import java.util.List;

import android.os.Parcelable;

import auto.parcel.AutoParcel;
import rx.functions.Func1;

/**
 * Created by Danil on 14.05.2015.
 */
@AutoParcel
public abstract class Config implements Parcelable {
    public abstract String imageBoxParameter();

    public abstract String replyUrl();

    public abstract String replyUrlText();

    public abstract List<MessengerConfigs> messengerConfigs();

    public abstract List<MessengerOrder> messengerOrders();

    public abstract List<GifMessengerOrder> gifMessengerOrders();

    public static Config create(String imageBoxParameter, String replyUrl, String replyUrlText,
                                List<MessengerConfigs> messengerConfigs, List<MessengerOrder> messengerOrders,
                                List<GifMessengerOrder> gifMessengerOrders) {
        return new AutoParcel_Config(imageBoxParameter, replyUrl, replyUrlText, messengerConfigs, messengerOrders,
                gifMessengerOrders);
    }

    public static Config from(RestConfig restConfig) {
        final String imageBoxParameter = restConfig.imageBoxParameter;
        final String replyUrl = restConfig.replyUrl;
        final String replyUrlText = restConfig.replyUrlText;
        final List<MessengerConfigs> messengerConfigs = restConfig.messengerConfigs;
        final List<MessengerOrder> messengerOrders = restConfig.messengerOrders;
        final List<GifMessengerOrder> gifMessengerOrders = restConfig.gifMessengerOrders;
        return new AutoParcel_Config(imageBoxParameter, replyUrl, replyUrlText, messengerConfigs, messengerOrders,
                gifMessengerOrders);
    }

    public static final Func1<RestConfig, Config> FROM_REST = new Func1<RestConfig, Config>() {
        @Override
        public Config call(RestConfig restConfig) {
            return from(restConfig);
        }
    };
}
