package com.umad.rly.data.api.response;

import java.util.List;

public final class CategoryResponse {
    public final List<Category> categories;
    public final List<Promo> promos;

    public CategoryResponse(List<Category> categories, List<Promo> promos) {
        this.categories = categories;
        this.promos = promos;
    }
}
