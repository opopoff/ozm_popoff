package com.ozm.rocks.data.api.request;

import java.util.List;

public class CategoryPinRequest {
    private List<Action> pins;

    public CategoryPinRequest(List<Action> pins) {
        this.pins = pins;
    }
}
