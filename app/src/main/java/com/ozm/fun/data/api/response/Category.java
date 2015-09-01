package com.ozm.fun.data.api.response;

import android.os.Parcel;
import android.os.Parcelable;

public final class Category implements Parcelable {
    public final long id;
    public final String backgroundImage;
    public final String description;
    public final boolean isPinned;
    public final boolean isPromo;
    public final boolean showNew;
    public final long promoEnd;
    public final String promoBackgroundImage;
    public final boolean isNew;

    public Category(long id, String backgroundImage, String description, boolean isPinned, boolean isPromo,
                    boolean showNew, long promoEnd, String promoBackgroundImage, boolean isNew) {
        this.id = id;
        this.backgroundImage = backgroundImage;
        this.description = description;
        this.isPinned = isPinned;
        this.isPromo = isPromo;
        this.showNew = showNew;
        this.promoEnd = promoEnd;
        this.promoBackgroundImage = promoBackgroundImage;
        this.isNew = isNew;
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
        dest.writeByte(showNew ? (byte) 1 : (byte) 0);
        dest.writeLong(this.promoEnd);
        dest.writeString(this.promoBackgroundImage);
        dest.writeByte(isNew ? (byte) 1 : (byte) 0);
    }

    protected Category(Parcel in) {
        this.id = in.readLong();
        this.backgroundImage = in.readString();
        this.description = in.readString();
        this.isPinned = in.readByte() != 0;
        this.isPromo = in.readByte() != 0;
        this.showNew = in.readByte() != 0;
        this.promoEnd = in.readLong();
        this.promoBackgroundImage = in.readString();
        this.isNew = in.readByte() != 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
