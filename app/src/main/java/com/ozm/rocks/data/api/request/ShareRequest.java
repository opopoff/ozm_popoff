package com.ozm.rocks.data.api.request;

import java.util.List;

public class ShareRequest {
    private List<Action> shares;

    public ShareRequest(List<Action> likes) {
        this.shares = likes;
    }
}
