package com.umad.wat.data.api.request;

import java.util.List;

public class HideRequest {
    private List<Action> hides;

    public HideRequest(List<Action> hides) {
        this.hides = hides;
    }

    public List<Action> getHides() {
        return hides;
    }
}
