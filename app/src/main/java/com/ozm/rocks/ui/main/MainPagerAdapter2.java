package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.ozm.rocks.util.ViewPagerAdapter;

public class MainPagerAdapter2 extends ViewPagerAdapter<MainScreens> {

    public MainPagerAdapter2(@NonNull Context context) {
        super(context);
    }

    public String getPageTitle(int position) {
        return getContext().getString(getItem(position).getNameResId());
    }

    @Override
    public int getCount() {
        return MainScreens.getList().size();
    }

    @Override
    protected int getItemLayoutId(MainScreens item) {
        return item.getResId();
    }

    @Override
    public void bindView(MainScreens item, int position, View view) {

    }

}
