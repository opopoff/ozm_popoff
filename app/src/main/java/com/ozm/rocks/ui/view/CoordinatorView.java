package com.ozm.rocks.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.util.view.SlidingTabLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CoordinatorView extends FrameLayout {

    @InjectView(R.id.ozome_toolbar)
    protected OzomeToolbar toolbar;

    @InjectView(R.id.coordinator_tabs)
    protected SlidingTabLayout mSlidingTabLayout;

    @InjectView(R.id.coordinator_pager)
    protected ViewPager mViewPager;

    private CoordinatorPageAdapter pageAdapter;

    public CoordinatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        mSlidingTabLayout.setDistributeEvenly(true);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void addScreens(List<CoordinatorPageAdapter.Item> pages) {
        pageAdapter = new CoordinatorPageAdapter(getContext());
        mViewPager.setOffscreenPageLimit(pages.size());
        mViewPager.setAdapter(pageAdapter);
        pageAdapter.addAll(pages);
        mSlidingTabLayout.setViewPager(mViewPager);
    };
}
