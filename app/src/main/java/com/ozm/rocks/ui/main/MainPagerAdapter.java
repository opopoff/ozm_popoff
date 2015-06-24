package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.ozm.rocks.util.ViewPagerAdapter;

public class MainPagerAdapter extends ViewPagerAdapter<MainPagerAdapter.Item> {

    public MainPagerAdapter(@NonNull Context context) {
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
