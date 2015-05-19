package com.ozm.rocks.data.api.request;

import java.util.List;

public class LikeRequest {
    private List<Action> likes;

    public LikeRequest(List<Action> likes) {
        this.likes = likes;
    }
}
