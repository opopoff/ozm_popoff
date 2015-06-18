package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.ozm.rocks.util.ViewPagerAdapter;

import java.util.List;

public class MainPagerAdapter extends ViewPagerAdapter<MainScreens>
        implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener{

    private final @NonNull Context mContext;
    private final @NonNull TabHost mTabHost;
    private final @NonNull ViewPager mViewPager;

    private OnPageChangeListener mOnPageChangeListener;

    public MainPagerAdapter(@NonNull Context context, @NonNull TabHost tabHost, @NonNull ViewPager viewPager) {

        super(context);
        this.mContext = context;
        this.mTabHost = tabHost;
        this.mViewPager = viewPager;

        mTabHost.setOnTabChangedListener(this);
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
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

    @Override
    public void addAll(List<MainScreens> list) {
        super.addAll(list);

        mTabHost.setup();
        for (MainScreens mainScreens : list) {
            final String name = mContext.getResources().getString(mainScreens.getNameResId());
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(name).setIndicator(name);
            tabSpec.setContent(new DummyTabFactory(mContext));
            mTabHost.addTab(tabSpec);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {

        // Unfortunately when TabHost changes the current tab, it kindly
        // also takes care of putting focus on it when not in touch mode.
        // The jerk.
        // This hack tries to prevent this from pulling focus out of our
        // ViewPager.

        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mTabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(position);
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        public DummyTabFactory(final Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(final String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public interface OnPageChangeListener extends ViewPager.OnPageChangeListener {

    }

}
