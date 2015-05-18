package com.ozm.rocks.data.api.request;

public class Action {

    private Long imageId;
    private Long time;
    private Long categorySource;
    private Long duration;
    private String applicationId;
    private Long categoryId;

    /**
     * Constructor for like, dislike, hide, hover, pinCategory and unpinCategory feed item
     *
     * @param imageId        image identificator
     * @param time           time stamp in millis in UTC example System.currentTimeMillis()/1000
     * @param categorySource param where from identificator
     * @param duration
     * @param applicationId  package name of application
     * @param categoryId     category identificator
     */
    private Action(Long imageId, Long time, Long categorySource, Long duration, String applicationId, Long categoryId) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = categorySource;
        this.duration = duration;
        this.applicationId = applicationId;
        this.categoryId = categoryId;
    }

    public Action getLikeDislikeHideAction(long imageId, long time, Long categorySource) {

        return new Action(imageId, time, categorySource, null, null, null);
    }

    public static Action getLikeDislikeHideActionForMainFeed(long imageId, long time) {

        return new Action(imageId, time, null, null, null, null);
    }

    public Action getShareAction(long imageId, long time, Long categorySource, String applicationId) {

        return new Action(imageId, time, categorySource, null, applicationId, null);
    }

    public Action getHoverAction(long imageId, long time, Long categorySource, Long duration) {

        return new Action(imageId, time, categorySource, duration, null, null);
    }

    public Action getPinUnpinAction(long time, Long categoryId) {

        return new Action(null, time, null, null, null, categoryId);
    }
}
