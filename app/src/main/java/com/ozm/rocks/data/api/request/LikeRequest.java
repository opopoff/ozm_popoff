package com.ozm.rocks.data.api.request;

import java.util.List;

public class LikeRequest {
    private List<LikeDislike> likes;

    public LikeRequest(List<LikeDislike> likes) {
        this.likes = likes;
    }
}
