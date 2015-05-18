package com.ozm.rocks.data.api.request;

import java.util.List;

public class CategoryUnpinRequest {
    private List<Action> categoryUnpins;

    public CategoryUnpinRequest(List<Action> unpins) {
        this.categoryUnpins = unpins;
    }
}
