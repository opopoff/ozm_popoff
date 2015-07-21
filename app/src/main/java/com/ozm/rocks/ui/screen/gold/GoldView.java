package com.ozm.rocks.ui.screen.gold;

import android.content.Context;
import android.support.v4.view.ViewPager;
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
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.screen.gold.favorite.GoldFavoriteView;
import com.ozm.rocks.ui.screen.gold.novel.GoldNovelView;
import com.ozm.rocks.ui.misc.CoordinatorPageAdapter;
import com.ozm.rocks.ui.misc.CoordinatorView;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.NetworkState;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldView extends FrameLayout implements BaseView {
    public static final long DURATION_ONBOARDING_ANIMATION = 500;
    public static final long DURATION_HIDE_DELAY_ONBOARDING_ANIMATION = 1000;

    @Inject
    GoldActivity.Presenter presenter;

    @Inject
    NetworkState mNetworkState;

    @Inject
    LocalyticsController localyticsController;

    @InjectView(R.id.ozome_toolbar)
    protected OzomeToolbar toolbar;
    @InjectView(R.id.gold_first_on_boarding)
    protected TextView goldFirstOnBoarding;
    @InjectView(R.id.gold_click_view)
    protected View clickView;
    @InjectView(R.id.coordinator_view)
    protected CoordinatorView coordinatorView;
    @InjectView(R.id.gold_like_text)
    protected TextView likeTextView;

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

        final List<CoordinatorPageAdapter.Item> pages = new LinkedList(GoldScreens.getList());
        if (!presenter.getCategory().showNew) {
            pages.remove(GoldScreens.NOVEL_SCREEN);
        }

        coordinatorView.addScreens(pages);
        coordinatorView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == .0f && positionOffsetPixels == 0) {
                    final GoldScreens screen = (GoldScreens) pages.get(position);
//                    if (screen == GoldScreens.FAVORITE_SCREEN) {
//                        localyticsController.openFavorites();
//                    } else
                    if (screen == GoldScreens.NOVEL_SCREEN) {
                        localyticsController.openNew(category.description);
                        final View childView = coordinatorView.getChildPageView(GoldScreens.NOVEL_SCREEN);
                        if (childView instanceof GoldNovelView) {
                            ((GoldNovelView) childView).showView();
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void setToolbarMenu(Category category, boolean isFirst) {

        if (!isFirst) {
            toolbar.inflateMenu(R.menu.gold);
            final MenuItem item = toolbar.getMenu().findItem(R.id.gold_menu_pick_up);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.gold_menu_pick_up) {
                        presenter.pin();
                        hideToolbarMenu();
                        return true;
                    }
                    return false;
                }
            });
            item.setVisible(!category.isPromo);
        }
    }

    public void hideToolbarMenu() {
        final Menu menu = toolbar.getMenu();
        menu.findItem(R.id.gold_menu_pick_up).setVisible(false);
    }

    public void showFourOnBoarding() {
        localyticsController.showPromptPinGoldenCollection();
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

    public void showPinMessage(final Category category) {
        String text;
        if (category.isPromo){
            text = getResources().getString(R.string.gold_pin_on_boarding_text_promo);
        } else {
            text = getResources().getString(R.string.gold_pin_on_boarding_text);
        }
        likeTextView.setText(text);
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation1.setDuration(DURATION_ONBOARDING_ANIMATION);
        ((View) likeTextView.getParent()).setVisibility(View.VISIBLE);
        alphaAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((View) likeTextView.getParent()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                        alphaAnimation.setDuration(DURATION_ONBOARDING_ANIMATION);
                        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                ((View) likeTextView.getParent()).setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        ((View) likeTextView.getParent()).startAnimation(alphaAnimation);
                    }
                }, DURATION_HIDE_DELAY_ONBOARDING_ANIMATION);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ((View) likeTextView.getParent()).startAnimation(alphaAnimation1);
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
        if (coordinatorView.getCurrentPagePosition() != 0) {
            coordinatorView.setCurrentPage(0);
            return true;
        }
        return false;
    }
}
