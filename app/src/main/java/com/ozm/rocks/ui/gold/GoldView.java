package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.gold.favorite.GoldFavoriteView;
import com.ozm.rocks.ui.misc.CoordinatorView;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.NetworkState;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldView extends FrameLayout implements BaseView {
    public static final long DURATION_ONBOARDING_ANIMATION = 500;

    @Inject
    GoldActivity.Presenter presenter;

    @Inject
    NetworkState mNetworkState;

    @InjectView(R.id.ozome_toolbar)
    protected OzomeToolbar toolbar;
    @InjectView(R.id.gold_first_on_boarding)
    protected TextView goldFirstOnBoarding;
    @InjectView(R.id.gold_click_view)
    protected View clickView;
    @InjectView(R.id.coordinator_view)
    protected CoordinatorView coordinatorView;

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

        final Category category = presenter.getCategory();
        toolbar.setTitle(category.description);
        setToolbarMenu(category, presenter.isFirst());
        coordinatorView.addScreens(GoldScreens.getList());
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
        final Menu menu = toolbar.getMenu();
        menu.findItem(R.id.gold_menu_pick_up).setVisible(false);
        menu.findItem(R.id.gold_menu_pin).setVisible(false);
    }

    public void showFirstOnBoarding() {
        goldFirstOnBoarding.setVisibility(VISIBLE);
        clickView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickView.setClickable(false);
                hideFirstOnBoarding();
            }
        });
    }

    public void hideFirstOnBoarding() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(DURATION_ONBOARDING_ANIMATION);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                goldFirstOnBoarding.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        goldFirstOnBoarding.startAnimation(alphaAnimation);
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

    public void moveItem(ImageResponse image) {
        final GoldFavoriteView childPageView = (GoldFavoriteView)
                coordinatorView.getChildPageView(GoldScreens.FAVORITE_SCREEN);
        childPageView.addResourceImage(image);
    }

    public boolean onBackPressed() {
        if (coordinatorView.getCurrentPagePosition() != 0){
            coordinatorView.setCurrentPage(0);
            return true;
        }
        return false;
    }
}
