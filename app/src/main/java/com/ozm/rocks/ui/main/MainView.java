package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.analytics.LocalyticsController;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainView extends LinearLayout implements BaseView {

    @Inject
    MainActivity.Presenter presenter;
    @Inject
    LocalyticsController localyticsController;

    @InjectView(R.id.tabhost)
    protected TabHost mTabHost;

    @InjectView(R.id.pager)
    protected ViewPager mViewPager;

    private MainPagerAdapter mMainPagerAdapter;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
//        mScreenButtonsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (mScreenPagerAdapter.getCount() > 0) {
//                    mMenuButton.setCheckState(false);
//                    final int position = mScreenPagerAdapter.getItemPositionById(checkedId);
//                    if (position >= 0) {
//                        mScreenPager.setCurrentItem(position, true);
//                        showMainContent();
//                    }
//                }
//            }
//        });
//        mScreenPager.setOffscreenPageLimit(2);
//        mScreenPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Timber.d("ScrollViewPager: onPageScrolled, position=%d, positionOffset=%f, onPageScrolled=%d",
//                        position, positionOffset, positionOffsetPixels);
//
//                if (positionOffset == .0f && positionOffsetPixels == 0) {
//                    final MainScreens screen = MainScreens.getList().get(position);
//                    if (screen == MainScreens.EMOTIONS_SCREEN) {
//                        localyticsController.openCategories();
//                    } else if (screen == MainScreens.FAVORITE_SCREEN) {
//                        localyticsController.openFavorites();
//                    } else if (screen == MainScreens.GENERAL_SCREEN) {
//                        localyticsController.openFeed(LocalyticsController.TAB);
//                    }
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Timber.d("ScrollViewPager: onPageSelected, position=%d", position);
//                updateCurrentButton(position);
//                if (mScreenPagerAdapter.getItem(position).getResId() == MainScreens.FAVORITE_SCREEN.getResId()) {
//                    presenter.updateMyFeed();
//                }
//                presenter.pageChanged();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Timber.d("ScrollViewPager: onPageScrollStateChanged, state=%d", state);
//            }
//        });
//
//        mMenuButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                localyticsController.openSettings();
//                mScreenButtonsGroup.clearCheck();
//                mScreenButtonsGroup.clearCheck();
//                mMenuButton.setCheckState(true);
//                showSettings();
//            }
//        });
//
//        initScreenButtons(MainScreens.getList());
//
//        mScreenPager.setAdapter(mScreenPagerAdapter);
//        mScreenPagerAdapter.addAll(MainScreens.getList());
//
//        updateCurrentButton(0);

        mMainPagerAdapter = new MainPagerAdapter(getContext(), mTabHost, mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mMainPagerAdapter);
        mMainPagerAdapter.addAll(MainScreens.getList());
        mMainPagerAdapter.setOnPageChangeListener(new MainPagerAdapter.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == .0f && positionOffsetPixels == 0) {
                    final MainScreens screen = MainScreens.getList().get(position);
                    if (screen == MainScreens.EMOTIONS_SCREEN) {
                        localyticsController.openCategories();
                    } else if (screen == MainScreens.FAVORITE_SCREEN) {
                        localyticsController.openFavorites();
                    } else if (screen == MainScreens.GENERAL_SCREEN) {
                        localyticsController.openFeed(LocalyticsController.TAB);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mMainPagerAdapter.getItem(position).getResId() == MainScreens.FAVORITE_SCREEN.getResId()) {
                    presenter.updateMyFeed();
                }
                presenter.pageChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

//    private void updateCurrentButton(int position) {
//        mScreenButtonsGroup.check(mScreenPagerAdapter.getItem(position).getButtonId());
//    }
//
//    private void initScreenButtons(List<MainScreens> screens) {
//        for (MainScreens screen : screens) {
//            RadioButtonCenter view = (RadioButtonCenter) layoutInflater.inflate(
//                    R.layout.radio_button_view, null);
//            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
//                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
////            view.setButtonDrawable(screen.getIconSelectorResId());
//            final Drawable drawable = ResourcesCompat.getDrawable(getResources(),
//                    screen.getIconSelectorResId(), getContext().getTheme());
//            view.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
//            view.setPadding(0, getResources().getDimensionPixelSize(R.dimen.tab_button_top_padding), 0, 0);
//            view.setLayoutParams(params);
//            view.setText(screen.getNameResId());
//            view.setId(screen.getButtonId());
//            mScreenButtonsGroup.addView(view);
//        }
//    }

    @Override
    public void showLoading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showContent() {
//        setDisplayedChildId(R.id.main_content_view);
    }

    @Override
    public void showError(Throwable throwable) {
        // TODO: implement no network error
    }

    public void showMainContent() {
//        mBetterViewAnimator.setDisplayedChildId(R.id.main_view_pager_container);
    }

    public void openFirstScreen() {
        showMainContent();
        mTabHost.setCurrentTab(0);
    }
}
