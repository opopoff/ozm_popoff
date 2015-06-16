package com.ozm.rocks.data.vk;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Danil on 16.06.2015.
 */
public class ApiVkMessage implements Parcelable {
    public ApiVkDialog message;

    public static Creator<ApiVkMessage> CREATOR = new Creator() {
        public ApiVkMessage createFromParcel(Parcel source) {
            return new ApiVkMessage(source);
        }

        public ApiVkMessage[] newArray(int size) {
            return new ApiVkMessage[size];
        }
    };

    public ApiVkMessage(JSONObject from) {
        this.parse(from);
    }

    public ApiVkMessage parse(JSONObject source) {
        this.message = new ApiVkDialog(source.optJSONObject("message"));
        return this;
    }

    public ApiVkMessage(Parcel in) {
//        this.user = in.read();
    }

    public ApiVkMessage() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.body);
    }
}
