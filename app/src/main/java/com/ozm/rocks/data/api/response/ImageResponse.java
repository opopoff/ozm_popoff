package com.ozm.rocks.data.api.response;

public final class ImageResponse {
    public final long id;
    public final String url;
    public final long categoryId;
    public final String categoryDescription;
    public boolean liked;
    public final boolean shared;
    public final long timeUsed;
    public final int width;
    public final int height;
    public final String mainColor;
    public final boolean isGIF;

    public ImageResponse(long id, String url, long categoryId,
                         String categoryDescription, boolean liked, boolean shared,
                         long timeUsed, int width, int height, String mainColor, boolean isGIF) {
        this.id = id;
        this.url = url;
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
}
