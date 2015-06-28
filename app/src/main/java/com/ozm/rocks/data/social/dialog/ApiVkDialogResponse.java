package com.ozm.rocks.data.social.dialog;

import android.os.Parcel;

import com.vk.sdk.api.model.VKApiModel;

import org.json.JSONObject;

/**
 * Created by Danil on 16.06.2015.
 */
public class ApiVkDialogResponse extends VKApiModel {
    public ApiVkDialogs dialogs;

    public static Creator<ApiVkDialogResponse> CREATOR = new Creator() {
        public ApiVkDialogResponse createFromParcel(Parcel source) {
            return new ApiVkDialogResponse(source);
        }

        public ApiVkDialogResponse[] newArray(int size) {
            return new ApiVkDialogResponse[size];
        }
    };

    public ApiVkDialogResponse(JSONObject from) {
        this.parse(from);
    }

    public ApiVkDialogResponse parse(JSONObject source) {
        this.dialogs = new ApiVkDialogs(source.optJSONObject("response"));
        return this;
    }

    public ApiVkDialogResponse(Parcel in) {
//        this.user = in.read();
    }

    public ApiVkDialogResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.body);
    }
}
