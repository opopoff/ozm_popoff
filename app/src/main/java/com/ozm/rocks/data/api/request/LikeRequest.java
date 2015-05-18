package com.ozm.rocks.data.api.request;

import java.util.List;

public class LikeRequest {
    private List<LikeDislikeHide> likes;

    public LikeRequest(List<LikeDislikeHide> likes) {
        this.likes = likes;
    }
}
