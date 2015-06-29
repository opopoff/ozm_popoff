package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.ui.misc.CoordinatorPageAdapter;
import com.ozm.rocks.ui.misc.CoordinatorView;
import com.ozm.rocks.ui.view.OzomeToolbar;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainView extends FrameLayout implements BaseView {

    @Inject
    MainActivity.Presenter presenter;

    @Inject
    LocalyticsController localyticsController;

    @InjectView(R.id.main_drawer_layout)
    protected DrawerLayout drawerLayout;

    @InjectView(R.id.coordinator_view)
    protected CoordinatorView coordinatorView;

    @InjectView(R.id.ozome_toolbar)
    protected OzomeToolbar toolbar;

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

        toolbar.setTitleVisibility(true);
        toolbar.setTitle(R.string.main_screen_title);
        toolbar.setLogoVisibility(false);
        toolbar.setDrawerIconVisibility(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        final List<CoordinatorPageAdapter.Item> pages = MainScreens.getList();
        coordinatorView.addScreens(pages);
        coordinatorView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == .0f && positionOffsetPixels == 0) {
                    final MainScreens screen = (MainScreens) pages.get(position);
                    if (screen == MainScreens.EMOTIONS_SCREEN) {
                        localyticsController.openCategories();
                    } else if (screen == MainScreens.FAVORITE_SCREEN) {
                        localyticsController.openFavorites();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (coordinatorView.getPageItem(position).getResId() == MainScreens.FAVORITE_SCREEN.getResId()) {
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
    }

    @Override
    public void showError(Throwable throwable) {
    }

    public void showMainContent() {
    }

    public void openFirstScreen() {
        showMainContent();
        coordinatorView.setCurrentPage(0);
    }

    public boolean onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        }
        if (coordinatorView.getCurrentPagePosition() != 0){
            coordinatorView.setCurrentPage(0);
            return true;
        }
        return false;
    }
}
