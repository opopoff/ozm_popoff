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
    public final boolean shared;
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

    public ImageResponse(long id, String url, String sharingUrl, long categoryId,
                         String categoryDescription, boolean liked, boolean shared,
                         long timeUsed, int width, int height, String mainColor, boolean isGIF,
                         String imageType, String videoUrl, String thumbnailUrl, int thumbnailWidth,
                         int thumbnailHeight) {
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
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (sharingUrl != null ? !sharingUrl.equals(that.sharingUrl) : that.sharingUrl != null)
            return false;
        if (categoryDescription != null
                ? !categoryDescription.equals(that.categoryDescription) : that.categoryDescription != null)
            return false;
        if (mainColor != null ? !mainColor.equals(that.mainColor) : that.mainColor != null)
            return false;
        if (videoUrl != null ? !videoUrl.equals(that.videoUrl) : that.videoUrl != null)
            return false;
        if (imageType != null ? !imageType.equals(that.imageType) : that.imageType != null)
            return false;
        return !(thumbnailUrl != null ? !thumbnailUrl.equals(that.thumbnailUrl) : that.thumbnailUrl != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (sharingUrl != null ? sharingUrl.hashCode() : 0);
        result = 31 * result + (int) (categoryId ^ (categoryId >>> 32));
        result = 31 * result + (categoryDescription != null ? categoryDescription.hashCode() : 0);
        result = 31 * result + (liked ? 1 : 0);
        result = 31 * result + (shared ? 1 : 0);
        result = 31 * result + (int) (timeUsed ^ (timeUsed >>> 32));
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (mainColor != null ? mainColor.hashCode() : 0);
        result = 31 * result + (isGIF ? 1 : 0);
        result = 31 * result + (videoUrl != null ? videoUrl.hashCode() : 0);
        result = 31 * result + (imageType != null ? imageType.hashCode() : 0);
        result = 31 * result + (thumbnailUrl != null ? thumbnailUrl.hashCode() : 0);
        result = 31 * result + thumbnailWidth;
        result = 31 * result + thumbnailHeight;
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
        dest.writeString(this.videoUrl);
        dest.writeString(imageType);
        dest.writeValue(thumbnailUrl);
        dest.writeInt(thumbnailWidth);
        dest.writeInt(thumbnailHeight);
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
        this.videoUrl = in.readString();
        this.imageType = in.readString();
        this.thumbnailUrl = (String) in.readValue(ClassLoader.getSystemClassLoader());
        this.thumbnailWidth = in.readInt();
        this.thumbnailHeight = in.readInt();
    }

    public static final Parcelable.Creator<ImageResponse> CREATOR = new Parcelable.Creator<ImageResponse>() {
        public ImageResponse createFromParcel(Parcel source) {
            return new ImageResponse(source);
        }

        public ImageResponse[] newArray(int size) {
            return new ImageResponse[size];
        }
    };
}
