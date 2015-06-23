package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.ui.main.MainPagerAdapter;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.view.SlidingTabLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldView extends FrameLayout implements BaseView {

    @Inject
    GoldActivity.Presenter presenter;

    @Inject
    NetworkState mNetworkState;

    @InjectView(R.id.ozome_toolbar)
    protected OzomeToolbar toolbar;

    @InjectView(R.id.gold_tabs)
    protected SlidingTabLayout mSlidingTabLayout;

    @InjectView(R.id.gold_pager)
    protected ViewPager mViewPager;

    private MainPagerAdapter mMainPagerAdapter;

    public GoldView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            GoldComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        toolbar.setTitleVisibility(true);
        toolbar.setLogoVisibility(false);
        toolbar.setNavigationIconVisibility(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.goBack();
            }
        });

        mMainPagerAdapter = new MainPagerAdapter(getContext());
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mMainPagerAdapter);
        mMainPagerAdapter.addAll(GoldScreens.getList());

        mSlidingTabLayout.setDistributeEvenly(true);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        mSlidingTabLayout.setViewPager(mViewPager);
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

    public void setToolbarMenu(Category category, boolean isFirst) {
            if (!isFirst) {
                toolbar.inflateMenu(R.menu.gold);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.gold_menu_pick_up) {
                            presenter.pin();
                            hideToolbarMenu();
                        } else if (menuItem.getItemId() == R.id.gold_menu_pin) {
                            presenter.pin();
                            hideToolbarMenu();
                        }
                        return false;
                    }
                });
                if (category.isPromo) {
                    toolbar.getMenu().findItem(R.id.gold_menu_pick_up).setVisible(false);
                } else {
                    toolbar.getMenu().findItem(R.id.gold_menu_pin).setVisible(false);
                }
            }
    }

    public void hideToolbarMenu() {
        toolbar.getMenu().findItem(R.id.gold_menu_pick_up).setVisible(false);
        toolbar.getMenu().findItem(R.id.gold_menu_pin).setVisible(false);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError(Throwable throwable) {

    }
}
