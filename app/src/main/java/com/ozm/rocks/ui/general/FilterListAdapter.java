package com.ozm.rocks.ui.general;

import android.content.Context;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.ui.misc.ListBindableAdapter;

import java.util.ArrayList;
import java.util.List;

public class FilterListAdapter extends ListBindableAdapter<FilterListItemData> {

    public static final long DEFAULT_ITEM_IT = 0;

    public FilterListAdapter(Context context) {
        super(context);
        ArrayList<FilterListItemData> items = new ArrayList<FilterListItemData>();
        items.add(createDefaultItem());
        addAll(items);
    }

    @Override
    public void bindView(FilterListItemData item, int position, View view) {
        ((FilterListItemView) view).bind(item);
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.category_filter_item_view;
    }

    @Override
    public void addAll(List<? extends FilterListItemData> items) {
        clear();
        ((List<FilterListItemData>) items).add(0, createDefaultItem());
        super.addAll(items);
    }

    private FilterListItemData createDefaultItem() {
        return new FilterListItemData(DEFAULT_ITEM_IT,
                getContext().getResources().getString(R.string.category_filter_first_item_title));
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public FilterListItemData getItemById(long id) {
        final List<FilterListItemData> list = getList();
        for (FilterListItemData item : list) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

}

