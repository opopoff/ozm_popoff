package com.umad.rly.ui.screen.main.general;

import com.umad.rly.data.api.response.Category;

import java.util.ArrayList;
import java.util.List;

public class FilterListItemData {
    public final long id;
    public final String title;

    public FilterListItemData(long id, String title) {
        this.title = title;
        this.id = id;
    }

    public static FilterListItemData from(Category category) {
        return new FilterListItemData(category.id, category.description);
    }

    public static List<FilterListItemData> from(List<Category> categories) {
        final ArrayList<FilterListItemData> items = new ArrayList<>();
        for (Category category : categories) {
            items.add(FilterListItemData.from(category));
        }
        return items;
    }
}
