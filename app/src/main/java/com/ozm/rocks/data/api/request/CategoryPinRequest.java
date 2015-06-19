package com.ozm.rocks.data.api.request;

import java.util.List;

public class CategoryPinRequest {
    private List<Action> categoryPins;

    public CategoryPinRequest(List<Action> pins) {
        this.categoryPins = pins;
    }

    public List<Action> getPins() {
        return categoryPins;
    }
}
