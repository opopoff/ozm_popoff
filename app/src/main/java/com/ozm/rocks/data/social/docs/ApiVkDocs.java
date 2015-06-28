package com.ozm.rocks.data.social.docs;

import android.os.Parcel;
import android.os.Parcelable;

import com.ozm.rocks.data.social.dialog.ApiVkMessage;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Danil on 16.06.2015.
 */
public class ApiVkDocs implements Parcelable {
    public int id;
    public int ownerId;
    public String tittle;
    public int size;
    public String ext;
    public String url;

    public static Creator<ApiVkDocs> CREATOR = new Creator() {
        public ApiVkDocs createFromParcel(Parcel source) {
            return new ApiVkDocs(source);
        }

        public ApiVkDocs[] newArray(int size) {
            return new ApiVkDocs[size];
        }
    };

    public ApiVkDocs(JSONObject from) {
        this.parse(from);
    }

    public ApiVkDocs parse(JSONObject source) {
        this.id = source.optInt("id");
        this.ownerId = source.optInt("owner_id");
        this.tittle = source.optString("tittle");
        this.size = source.optInt("size");
        this.ext = source.optString("ext");
        this.url = source.optString("url");
        return this;
    }

    public ApiVkDocs(Parcel in) {
//        this.count = in.readInt();
//        this.items = in.create();
    }

    public ApiVkDocs() {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(this.count);
//        dest.writeIntArray(this.items);
    }
}
