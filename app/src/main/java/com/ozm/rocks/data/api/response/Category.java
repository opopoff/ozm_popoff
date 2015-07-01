package com.ozm.rocks.data.api.response;

import android.os.Parcel;
import android.os.Parcelable;

public final class Category implements Parcelable {
    public final long id;
    public final String backgroundImage;
    public final String description;
    public final boolean isPinned;
    public final boolean isPromo;
    public final long promoEnd;

    public Category(long id, String backgroundImage, String description, boolean isPinned, boolean isPromo,
                    long promoEnd) {
        this.id = id;
        this.backgroundImage = backgroundImage;
        this.description = description;
        this.isPinned = isPinned;
        this.isPromo = isPromo;
        this.promoEnd = promoEnd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.backgroundImage);
        dest.writeString(this.description);
        dest.writeByte(isPinned ? (byte) 1 : (byte) 0);
        dest.writeByte(isPromo ? (byte) 1 : (byte) 0);
        dest.writeLong(this.promoEnd);
    }

    protected Category(Parcel in) {
        this.id = in.readLong();
        this.backgroundImage = in.readString();
        this.description = in.readString();
        this.isPinned = in.readByte() != 0;
        this.isPromo = in.readByte() != 0;
        this.promoEnd = in.readLong();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
