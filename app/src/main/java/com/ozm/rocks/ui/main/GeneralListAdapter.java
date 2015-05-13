package com.ozm.rocks.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ozm.R;
import com.ozm.rocks.ui.misc.BindableAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by dmitry on 12/05/15.
 */
public class GeneralListAdapter extends BindableAdapter<String> {
    private List<String> list = Collections.emptyList();

    public GeneralListAdapter(Context context) {
        super(context);
    }

    public void updateAll(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.main_general_list_item_view, container, false);
    }

    @Override
    public void bindView(String item, int position, View view) {
        ((GeneralListItemView) view).bindTo(item);
    }
}
