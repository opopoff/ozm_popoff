package com.ozm.rocks.data.social.docs;

import android.os.Parcel;

import com.ozm.rocks.data.social.dialog.ApiVkDialogs;
import com.ozm.rocks.data.social.dialog.ApiVkMessage;
import com.vk.sdk.api.model.VKApiModel;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Danil on 16.06.2015.
 */
public class ApiVkDocsResponse extends VKApiModel {
    public ApiVkDocs[] items;

    public static Creator<ApiVkDocsResponse> CREATOR = new Creator() {
        public ApiVkDocsResponse createFromParcel(Parcel source) {
            return new ApiVkDocsResponse(source);
        }

        public ApiVkDocsResponse[] newArray(int size) {
            return new ApiVkDocsResponse[size];
        }
    };

    public ApiVkDocsResponse(JSONObject from) {
        this.parse(from);
    }

    public ApiVkDocsResponse parse(JSONObject source) {
        JSONArray items = source.optJSONArray("");
        if (items != null) {
            this.items = new ApiVkDocs[items.length()];
            for (int i = 0; i < this.items.length; ++i) {
                this.items[i] = new ApiVkDocs(items.optJSONObject(i));
            }
        }
        return this;
    }

    public ApiVkDocsResponse(Parcel in) {
//        this.user = in.read();
    }

    public ApiVkDocsResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.body);
    }
}
