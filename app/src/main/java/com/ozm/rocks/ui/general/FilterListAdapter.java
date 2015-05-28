package com.ozm.rocks.ui.general;

import android.content.Context;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.ui.misc.ListBindableAdapter;

import java.util.List;

public class FilterListAdapter extends ListBindableAdapter<Category> {

    public FilterListAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(Category item, int position, View view) {
        ((FilterListItemView) view).bind(item);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.category_filter_item_view;
    }

    @Override
    public void addAll(List<? extends Category> items) {
        super.addAll(items);
    }

}

