package com.ozm.rocks.data.api.response;

import android.os.Parcel;
import android.os.Parcelable;

public final class ImageResponse implements Parcelable {
    public final long id;
    public final String url;
    public final String sharingUrl;
    public final long categoryId;
    public final String categoryDescription;
    public boolean liked;
    public boolean shared;
    public final long timeUsed;
    public final int width;
    public final int height;
    public final String mainColor;
    public final boolean isGIF;
    public final String videoUrl;
    public final String imageType;
    public final String thumbnailUrl;
    public final int thumbnailWidth;
    public final int thumbnailHeight;
    public boolean isNew;
    public boolean isNewBlink;

    public ImageResponse(long id, String url, String sharingUrl, long categoryId,
                         String categoryDescription, boolean liked, boolean shared,
                         long timeUsed, int width, int height, String mainColor, boolean isGIF,
                         String imageType, String videoUrl, String thumbnailUrl, int thumbnailWidth,
                         int thumbnailHeight, boolean isNew, boolean isNewBlink) {
        this.id = id;
        this.url = url;
        this.sharingUrl = sharingUrl;
        this.categoryId = categoryId;
        this.categoryDescription = categoryDescription;
        this.liked = liked;
        this.shared = shared;
        this.timeUsed = timeUsed;
        this.width = width;
        this.height = height;
        this.mainColor = mainColor;
        this.isGIF = isGIF;
        this.videoUrl = videoUrl;
        this.imageType = imageType;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
        this.isNew = isNew;
        this.isNewBlink = isNewBlink;
    }

    public  ImageResponse(ImageResponse another) {
        this.id = another.id;
        this.url = another.url;
        this.sharingUrl = another.sharingUrl;
        this.categoryId = another.categoryId;
        this.categoryDescription = another.categoryDescription;
        this.liked = another.liked;
        this.shared = another.shared;
        this.timeUsed = another.timeUsed;
        this.width = another.width;
        this.height = another.height;
        this.mainColor = another.mainColor;
        this.isGIF = another.isGIF;
        this.videoUrl = another.videoUrl;
        this.imageType = another.imageType;
        this.thumbnailUrl = another.thumbnailUrl;
        this.thumbnailWidth = another.thumbnailWidth;
        this.thumbnailHeight = another.thumbnailHeight;
        this.isNew = another.isNew;
        this.isNewBlink = another.isNewBlink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageResponse that = (ImageResponse) o;

        if (id != that.id) return false;
        if (categoryId != that.categoryId) return false;
        if (liked != that.liked) return false;
        if (shared != that.shared) return false;
        if (timeUsed != that.timeUsed) return false;
        if (width != that.width) return false;
        if (height != that.height) return false;
        if (isGIF != that.isGIF) return false;
        if (thumbnailWidth != that.thumbnailWidth) return false;
        if (thumbnailHeight != that.thumbnailHeight) return false;
        if (isNew != that.isNew) return false;
        if (isNewBlink != that.isNewBlink) return false;
        if (!url.equals(that.url)) return false;
        if (!sharingUrl.equals(that.sharingUrl)) return false;
        if (!categoryDescription.equals(that.categoryDescription)) return false;
        if (!mainColor.equals(that.mainColor)) return false;
        if (!videoUrl.equals(that.videoUrl)) return false;
        if (!imageType.equals(that.imageType)) return false;
        return thumbnailUrl.equals(that.thumbnailUrl);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + url.hashCode();
        result = 31 * result + sharingUrl.hashCode();
        result = 31 * result + (int) (categoryId ^ (categoryId >>> 32));
        result = 31 * result + categoryDescription.hashCode();
        result = 31 * result + (liked ? 1 : 0);
        result = 31 * result + (shared ? 1 : 0);
        result = 31 * result + (int) (timeUsed ^ (timeUsed >>> 32));
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + mainColor.hashCode();
        result = 31 * result + (isGIF ? 1 : 0);
        result = 31 * result + videoUrl.hashCode();
        result = 31 * result + imageType.hashCode();
        result = 31 * result + thumbnailUrl.hashCode();
        result = 31 * result + thumbnailWidth;
        result = 31 * result + thumbnailHeight;
        result = 31 * result + (isNew ? 1 : 0);
        result = 31 * result + (isNewBlink ? 1 : 0);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeString(this.sharingUrl);
        dest.writeLong(this.categoryId);
        dest.writeString(this.categoryDescription);
        dest.writeByte(liked ? (byte) 1 : (byte) 0);
        dest.writeByte(shared ? (byte) 1 : (byte) 0);
        dest.writeLong(this.timeUsed);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.mainColor);
        dest.writeByte(isGIF ? (byte) 1 : (byte) 0);
        dest.writeValue(this.videoUrl);
//        dest.writeString(this.videoUrl);
        dest.writeValue(this.imageType);
//        dest.writeString(this.imageType);
        dest.writeString(this.thumbnailUrl);
        dest.writeInt(this.thumbnailWidth);
        dest.writeInt(this.thumbnailHeight);
        dest.writeByte(isNew ? (byte) 1 : (byte) 0);
        dest.writeByte(isNewBlink ? (byte) 1 : (byte) 0);
    }

    protected ImageResponse(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.sharingUrl = in.readString();
        this.categoryId = in.readLong();
        this.categoryDescription = in.readString();
        this.liked = in.readByte() != 0;
        this.shared = in.readByte() != 0;
        this.timeUsed = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.mainColor = in.readString();
        this.isGIF = in.readByte() != 0;
        this.videoUrl = (String) in.readValue(String.class.getClassLoader());
        this.imageType = (String) in.readValue(String.class.getClassLoader());
        this.thumbnailUrl = in.readString();
        this.thumbnailWidth = in.readInt();
        this.thumbnailHeight = in.readInt();
        this.isNew = in.readByte() != 0;
        this.isNewBlink = in.readByte() != 0;
    }

    @Override
    public String toString() {
        return "ImageResponse{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", sharingUrl='" + sharingUrl + '\'' +
                ", categoryId=" + categoryId +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", liked=" + liked +
                ", shared=" + shared +
                ", timeUsed=" + timeUsed +
                ", width=" + width +
                ", height=" + height +
                ", mainColor='" + mainColor + '\'' +
                ", isGIF=" + isGIF +
                ", videoUrl='" + videoUrl + '\'' +
                ", imageType='" + imageType + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", thumbnailWidth=" + thumbnailWidth +
                ", thumbnailHeight=" + thumbnailHeight +
                ", isNew=" + isNew +
                ", isNewBlink=" + isNewBlink +
                '}';
    }

    public static final Creator<ImageResponse> CREATOR = new Creator<ImageResponse>() {
        public ImageResponse createFromParcel(Parcel source) {
            return new ImageResponse(source);
        }

        public ImageResponse[] newArray(int size) {
            return new ImageResponse[size];
        }
    };
}
