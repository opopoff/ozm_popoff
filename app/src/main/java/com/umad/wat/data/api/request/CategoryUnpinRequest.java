package com.umad.wat.data.api.request;

import java.util.List;

public class CategoryUnpinRequest {
    private List<Action> categoryUnpins;

    public CategoryUnpinRequest(List<Action> unpins) {
        this.categoryUnpins = unpins;
    }

    public List<Action> getCategoryUnpins() {
        return categoryUnpins;
    }
}
