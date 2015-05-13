package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.ui.misc.BetterViewAnimator;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainView extends BetterViewAnimator implements BaseView {
    @Inject
    MainActivity.Presenter presenter;

    @InjectView(R.id.main_screen_pager)
    ViewPager mScreenPager;
    @InjectView(R.id.main_screen_buttons_group)
    RadioGroup mScreenButtonsGroup;

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
                    mScreenPager.setCurrentItem(mScreenPagerAdapter.getItemPositionById(checkedId), true);
                }
            }
        });
        mScreenPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateCurrentButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

    private void initScreenButtons(List<MainScreens> screenses) {
        for (MainScreens screens : screenses) {
            RadioButton view = (RadioButton) layoutInflater.inflate(R.layout.main_screen_button_item, null);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            view.setLayoutParams(params);
            view.setText(screens.name());
            view.setId(screens.getButtonId());
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

    public void openMenu() {
        removeView(ButterKnife.findById(this, R.id.main_content_view));
        layoutInflater.inflate(R.layout.main_emotions_view, this);
        showContent();
    }

    public void openLogin() {
        removeView(ButterKnife.findById(this, R.id.main_content_view));
        layoutInflater.inflate(R.layout.main_general_view, this);
        showContent();
    }

    public ScreenPagerAdapter getScreenPagerAdapter() {
        return mScreenPagerAdapter;
    }
}
