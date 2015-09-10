package com.umad.wat.data.api.request;

import java.util.List;

public class HoverRequest {
    private List<Action> hovers;

    public HoverRequest(List<Action> hovers) {
        this.hovers = hovers;
    }

    public List<Action> getHovers() {
        return hovers;
    }
}
