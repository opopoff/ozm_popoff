package com.ozm.rocks.data.api.request;

import java.util.List;

public class HideRequest {
    private List<LikeDislikeHide> hides;

    public HideRequest(List<LikeDislikeHide> likes) {
        this.hides = likes;
    }
}
