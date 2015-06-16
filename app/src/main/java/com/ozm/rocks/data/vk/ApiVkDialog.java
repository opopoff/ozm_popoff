package com.ozm.rocks.data.vk;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Danil on 16.06.2015.
 */
public class ApiVkDialog implements Parcelable {
    public int id;
    public long date;
    public int out;
    public int user_id;
    public int read_state;
    public String tittle;
    public String body;

    public static Creator<ApiVkDialog> CREATOR = new Creator() {
        public ApiVkDialog createFromParcel(Parcel source) {
            return new ApiVkDialog(source);
        }

        public ApiVkDialog[] newArray(int size) {
            return new ApiVkDialog[size];
        }
    };

    public ApiVkDialog(JSONObject from) {
        this.parse(from);
    }

    public ApiVkDialog parse(JSONObject source) {
        this.id = source.optInt("count");
        this.date = source.optLong("date");
        this.out = source.optInt("out");
        this.user_id = source.optInt("user_id");
        this.read_state = source.optInt("read_state");
        this.tittle = source.optString("tittle");
        this.body = source.optString("body");
        return this;
    }

    public ApiVkDialog(Parcel in) {
        this.id = in.readInt();
        this.date = in.readLong();
        this.out = in.readInt();
        this.user_id = in.readInt();
        this.read_state = in.readInt();
        this.tittle = in.readString();
        this.body = in.readString();
    }

    public ApiVkDialog() {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.date);
        dest.writeInt(this.out);
        dest.writeInt(this.user_id);
        dest.writeInt(this.read_state);
        dest.writeString(this.tittle);
        dest.writeString(this.body);
    }
}
