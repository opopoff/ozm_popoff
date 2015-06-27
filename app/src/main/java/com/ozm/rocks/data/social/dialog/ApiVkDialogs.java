package com.ozm.rocks.data.social.dialog;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Danil on 16.06.2015.
 */
public class ApiVkDialogs implements Parcelable {
    public int count;
    public ApiVkMessage[] items;
    public static Parcelable.Creator<ApiVkDialogs> CREATOR = new Parcelable.Creator() {
        public ApiVkDialogs createFromParcel(Parcel source) {
            return new ApiVkDialogs(source);
        }

        public ApiVkDialogs[] newArray(int size) {
            return new ApiVkDialogs[size];
        }
    };

    public ApiVkDialogs(JSONObject from) {
        this.parse(from);
    }

    public ApiVkDialogs parse(JSONObject source) {
        this.count = source.optInt("count");
        JSONArray items = source.optJSONArray("items");
        if (items != null) {
            this.items = new ApiVkMessage[items.length()];
            for (int i = 0; i < this.items.length; ++i) {
                this.items[i] = new ApiVkMessage(items.optJSONObject(i));
            }
        }

        return this;
    }

    public ApiVkDialogs(Parcel in) {
        this.count = in.readInt();
//        this.items = in.create();
    }

    public ApiVkDialogs() {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
//        dest.writeIntArray(this.items);
    }
}
