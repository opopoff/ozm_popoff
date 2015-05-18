package com.ozm.rocks.data.api.request;

import java.util.List;

public class DislikeRequest {
    private List<LikeDislikeHide> dislikes;

    public DislikeRequest(List<LikeDislikeHide> likes) {
        this.dislikes = likes;
    }
}
