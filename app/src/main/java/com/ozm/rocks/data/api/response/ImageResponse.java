package com.ozm.rocks.data.api.response;

public final class ImageResponse {
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

    public ImageResponse(long id, String url, String sharingUrl, long categoryId,
                         String categoryDescription, boolean liked, boolean shared,
                         long timeUsed, int width, int height, String mainColor, boolean isGIF) {
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageResponse)) return false;

        ImageResponse that = (ImageResponse) o;

        if (id != that.id) return false;
        if (categoryId != that.categoryId) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (sharingUrl != null ? !sharingUrl.equals(that.sharingUrl) : that.sharingUrl != null) return false;
        return !(categoryDescription != null ? !categoryDescription.equals(that.categoryDescription) : that.categoryDescription != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (sharingUrl != null ? sharingUrl.hashCode() : 0);
        result = 31 * result + (int) (categoryId ^ (categoryId >>> 32));
        result = 31 * result + (categoryDescription != null ? categoryDescription.hashCode() : 0);
        return result;
    }
}
