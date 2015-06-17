package com.ozm.rocks.data.vk;

import android.os.Parcel;

import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Danil on 16.06.2015.
 */
public class ApiVkUserResponse extends VKApiModel {
    public VKApiUser user;

    public static Creator<ApiVkUserResponse> CREATOR = new Creator() {
        public ApiVkUserResponse createFromParcel(Parcel source) {
            return new ApiVkUserResponse(source);
        }

        public ApiVkUserResponse[] newArray(int size) {
            return new ApiVkUserResponse[size];
        }
    };

    public ApiVkUserResponse(JSONObject from) {
        this.parse(from);
    }

    public ApiVkUserResponse parse(JSONObject source) {
        try {
            this.user = new VKApiUser(source.optJSONObject("response"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ApiVkUserResponse(Parcel in) {
//        this.user = in.read();
    }

    public ApiVkUserResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.body);
    }
}
