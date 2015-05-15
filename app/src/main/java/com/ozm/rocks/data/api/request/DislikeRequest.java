package com.ozm.rocks.data.api.request;

import java.util.List;

public class DislikeRequest {
    private List<LikeDislike> dislikes;

    public DislikeRequest(List<LikeDislike> likes) {
        this.dislikes = likes;
    }
}
