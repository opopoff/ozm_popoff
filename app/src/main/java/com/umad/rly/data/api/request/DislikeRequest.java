package com.umad.rly.data.api.request;

import java.util.List;

public class DislikeRequest {
    private List<Action> dislikes;

    public DislikeRequest(List<Action> dislikes) {
        this.dislikes = dislikes;
    }

    public List<Action> getDislikes() {
        return dislikes;
    }
}
