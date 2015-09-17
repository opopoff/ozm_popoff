package com.umad.wat.data.api.request;

public class Action {

    private Long imageId;
    private Long time;
    private Long categorySource;
    private Boolean categorySourcePersonal;
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
    private Action(Long imageId, Long time, Long categorySource, Boolean categorySourcePersonal, Long duration, String
            applicationId, Long
                           categoryId) {
        this.imageId = imageId;
        this.time = time;
        this.categorySource = categorySource;
        this.duration = duration;
        this.applicationId = applicationId;
        this.categoryId = categoryId;
        this.categorySourcePersonal = categorySourcePersonal;
    }

    public static Action getLikeDislikeHideAction(long imageId, long time, Long categorySource) {

        return new Action(imageId, time, categorySource, false, null, null, null);
    }

    public static Action getLikeDislikeHideActionForMainFeed(long imageId, long time) {

        return new Action(imageId, time, null, false, null, null, null);
    }

    public static Action getLikeDislikeHideActionForPersonal(long imageId, long time) {

        return new Action(imageId, time, null, true, null, null, null);
    }

    public static Action getLikeDislikeHideActionForGoldenPersonal(long imageId, long time, Long categorySource) {

        return new Action(imageId, time, categorySource, true, null, null, null);
    }

    public static Action getLikeDislikeHideActionForGoldenRandom(long imageId, long time, Long categorySource) {

        return new Action(imageId, time, categorySource, false, null, null, null);
    }

    public static Action getShareAction(long imageId, long time, Long categorySource, String applicationId) {

        return new Action(imageId, time, categorySource, false, null, applicationId, null);
    }

    public static Action getShareActionForMainFeed(long imageId, long time, String applicationId) {

        return new Action(imageId, time, null, false, null, applicationId, null);
    }

    public static Action getShareActionForPersonal(long imageId, long time, String applicationId) {

        return new Action(imageId, time, null, true, null, applicationId, null);
    }

    public static Action getShareActionForGoldenPersonal(long imageId, long time, Long categorySource, String
            applicationId) {

        return new Action(imageId, time, categorySource, true, null, applicationId, null);
    }

    public static Action getShareActionForGoldenRandom(long imageId, long time, Long categorySource, String
            applicationId) {

        return new Action(imageId, time, categorySource, false, null, applicationId, null);
    }

    public static Action getHoverAction(long imageId, long time, Long categorySource, Long duration) {

        return new Action(imageId, time, categorySource, false, duration, null, null);
    }

    public static Action getPinUnpinAction(long time, Long categoryId) {

        return new Action(null, time, null, false, null, null, categoryId);
    }

    public Long getImageId() {
        return imageId;
    }

    public Long getTime() {
        return time;
    }

    public Long getCategorySource() {
        return categorySource;
    }

    public Long getDuration() {
        return duration;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Boolean getCategorySourcePersonal() {
        return categorySourcePersonal;
    }
}
