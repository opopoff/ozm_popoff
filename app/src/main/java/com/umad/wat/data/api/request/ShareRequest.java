package com.umad.wat.data.api.request;

import java.util.List;

public class ShareRequest {
    private List<Action> shares;

    public ShareRequest(List<Action> likes) {
        this.shares = likes;
    }

    public List<Action> getShares() {
        return shares;
    }
}
