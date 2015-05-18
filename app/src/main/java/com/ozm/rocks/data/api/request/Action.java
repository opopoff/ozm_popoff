package com.ozm.rocks.data.api.request;

public class Action {

    private long imageId;
    private long time;
    private Long categorySource;
    private String applicationId;

    /**
     * Constructor for like, dislike and hide feed item
     *
     * @param imageId
     * @param time           time stamp in millis in UTC example System.currentTimeMillis()/1000
     * @param categorySource param where from identificator
     */
    public Action(long imageId, long time, long categorySource) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = categorySource;
        this.applicationId = null;
    }

    /**
     * Constructor for like, dislike and hide feed item without categorySource for General Feed
     *
     * @param imageId
     * @param time    time stamp in millis in UTC example System.currentTimeMillis()/1000
     */
    public Action(long imageId, long time) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = null;
        this.applicationId = null;
    }

    /**
     * Constructor for share feed item
     *
     * @param imageId
     * @param time    time stamp in millis in UTC example System.currentTimeMillis()/1000
     */
    public Action(long imageId, long time, long categorySource, String applicationId) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = categorySource;
        this.applicationId = applicationId;
    }

    /**
     * Constructor for share feed item without categorySource for General Feed
     *
     * @param imageId
     * @param time    time stamp in millis in UTC example System.currentTimeMillis()/1000
     */
    public Action(long imageId, long time, String applicationId) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = null;
        this.applicationId = applicationId;
    }
}
