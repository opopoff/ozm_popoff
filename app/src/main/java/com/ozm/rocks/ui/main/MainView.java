package com.ozm.rocks.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.ui.misc.BetterViewAnimator;
import com.ozm.rocks.util.RadioButtonCenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainView extends BetterViewAnimator implements BaseView {

    @Inject
    MainActivity.Presenter presenter;

    @InjectView(R.id.main_screen_pager)
    protected ViewPager mScreenPager;
    @InjectView(R.id.main_screen_buttons_group)
    protected RadioGroup mScreenButtonsGroup;
    @InjectView(R.id.no_internet_view)
    protected View mNoInternetView;
    @InjectView(R.id.main_better_view_animator)
    protected BetterViewAnimator mBetterViewAnimator;
    @InjectView(R.id.main_screen_menu_button)
    protected MainMenuButton mMenuButton;

    private ScreenPagerAdapter mScreenPagerAdapter;

    private final LayoutInflater layoutInflater;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        layoutInflater = LayoutInflater.from(context);
        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }

        mScreenPagerAdapter = new ScreenPagerAdapter(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

//        final boolean loggedIn = presenter.isLoggedIn();
//        Timber.d("User is %s, opening %s", loggedIn ? "logged in" : "not logged in",
//                loggedIn ? "menu" : "login");
//        layoutInflater.inflate(loggedIn ?
//                R.layout.main_emotions_view : R.layout.main_general_view, this);


        mScreenButtonsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mScreenPagerAdapter.getCount() > 0) {
                    mMenuButton.setCheckState(false);
                    final int position = mScreenPagerAdapter.getItemPositionById(checkedId);
                    if (position >= 0) {
                        mScreenPager.setCurrentItem(position, true);
                        showMainContent();
                    }
                }
            }
        });
        mScreenPager.setOffscreenPageLimit(2);
        mScreenPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateCurrentButton(position);
                if (mScreenPagerAdapter.getItem(position).getResId() == MainScreens.MY_COLLECTION_SCREEN.getResId()) {
                    presenter.updateMyFeed();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreenButtonsGroup.clearCheck();
                mScreenButtonsGroup.clearCheck();
                mMenuButton.setCheckState(true);
                showSettings();
            }
        });

        initScreenButtons(MainScreens.getList());

        mScreenPager.setAdapter(mScreenPagerAdapter);
        mScreenPagerAdapter.addAll(MainScreens.getList());

        updateCurrentButton(0);
    }

    private void updateCurrentButton(int position) {
        mScreenButtonsGroup.check(mScreenPagerAdapter.getItem(position).getButtonId());
    }

    private void initScreenButtons(List<MainScreens> screens) {
        for (MainScreens screen : screens) {
            RadioButtonCenter view = (RadioButtonCenter) layoutInflater.inflate(
                    R.layout.main_screen_button_item, null);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
//            view.setButtonDrawable(screen.getIconSelectorResId());
            final Drawable drawable = ResourcesCompat.getDrawable(getResources(),
                    screen.getIconSelectorResId(), getContext().getTheme());
            view.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            view.setPadding(0, getResources().getDimensionPixelSize(R.dimen.tab_button_top_padding), 0, 0);
            view.setLayoutParams(params);
            view.setText(screen.getNameResId());
            view.setId(screen.getButtonId());
            mScreenButtonsGroup.addView(view);
        }
    }

    @Override
    public void showLoading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showContent() {
        setDisplayedChildId(R.id.main_content_view);
    }

    @Override
    public void showError(Throwable throwable) {
        // TODO: implement no network error
    }

    public void showSettings() {
        mBetterViewAnimator.setDisplayedChildId(R.id.main_settings_container);
    }

    public void showMainContent() {
        mBetterViewAnimator.setDisplayedChildId(R.id.main_view_pager_container);
    }

    public void openFirstScreen() {
        showMainContent();
        mScreenPager.setCurrentItem(0);
    }
}
