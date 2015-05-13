package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.ozm.rocks.util.ViewPagerAdapter;

public class ScreenPagerAdapter extends ViewPagerAdapter<MainScreens> {
    public ScreenPagerAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(MainScreens item) {
        return item.getResId();
    }

    @Override
    public void bindView(MainScreens item, int position, View view) {

    }

    public int getItemPositionById(int checkedId) {
        for (MainScreens screen : mList) {
            if (screen.getButtonId() == checkedId) {
                return mList.indexOf(screen);
            }
        }
        return 0;
    }
}
