package com.ozm.rocks.data.api.request;

public class LikeDislike {

    private long imageId;
    private long time;
    private long categorySource;

    public LikeDislike(long imageId, long time, long categorySource) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = categorySource;
    }
}
