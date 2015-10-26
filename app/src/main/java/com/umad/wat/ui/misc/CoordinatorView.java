package com.umad.wat.ui.misc;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.umad.R;
import com.umad.wat.ui.view.OzomeToolbar;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CoordinatorView extends FrameLayout {

    @InjectView(R.id.ozome_toolbar)
    protected OzomeToolbar toolbar;

    @InjectView(R.id.coordinator_tab_layout)
    TabLayout tabLayout;

    @InjectView(R.id.coordinator_pager)
    protected ViewPager viewPager;

    private CoordinatorPageAdapter pageAdapter;

    public CoordinatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
    }

    public View getChildPageView(CoordinatorPageAdapter.Item item) {
        return viewPager.findViewWithTag(String.valueOf(item.getResId()));
    }

    public void addScreens(List<CoordinatorPageAdapter.Item> pages) {
        pageAdapter = new CoordinatorPageAdapter(getContext());
        viewPager.setOffscreenPageLimit(pages.size());
        viewPager.setAdapter(pageAdapter);
        pageAdapter.addAll(pages);
        tabLayout.setupWithViewPager(viewPager);
        if (tabLayout.getTabCount() == 1) {
            tabLayout.setSelectedTabIndicatorColor(
                    ContextCompat.getColor(getContext(), android.R.color.transparent));
        } else {
            tabLayout.setSelectedTabIndicatorColor(
                    ContextCompat.getColor(getContext(), android.R.color.white));
        }
    }

    public void setCurrentPage(int page) {
        viewPager.setCurrentItem(page, true);
    }

    public int getCurrentPagePosition() {
        return viewPager.getCurrentItem();
    }

    public CoordinatorPageAdapter.Item getPageItem(int position) {
        return pageAdapter.getItem(position);
    }
}
