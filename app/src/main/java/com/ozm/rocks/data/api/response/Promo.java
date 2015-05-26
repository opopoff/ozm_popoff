package com.ozm.rocks.data.api.response;

import java.util.List;

public final class Promo {
    public final String description;
    public final int line;
    public final List<Category> categories;

    public Promo(String description, int line, List<Category> categories) {
        this.description = description;
        this.line = line;
        this.categories = categories;
    }
}
