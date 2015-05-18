package com.ozm.rocks.data.api.request;

public class LikeDislike {

    private long imageId;
    private long time;
    private Long categorySource;

    public LikeDislike(long imageId, long time, long categorySource) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = categorySource;
    }

    /**
     * Constructor without categorySource for general feed
     * @param imageId
     * @param time
     */
    public LikeDislike(long imageId, long time) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = null;
    }
}
