package com.ozm.rocks.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.ozm.rocks.util.ViewPagerAdapter;

public class CoordinatorPageAdapter extends ViewPagerAdapter<CoordinatorPageAdapter.Item> {

    public CoordinatorPageAdapter(@NonNull Context context) {
        super(context);
    }

    public String getPageTitle(int position) {
        return getContext().getString(getItem(position).getNameResId());
    }

    @Override
    protected int getItemLayoutId(Item item) {
        return item.getResId();
    }

    @Override
    public void bindView(Item item, int position, View view) {

    }

    public static interface Item {
        int getResId();
        int getNameResId();
    }
}