package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.ui.view.CoordinatorView;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.NetworkState;

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

    @InjectView(R.id.gold_first_on_boarding)
    protected TextView goldFirstOnBoarding;

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
        toolbar.getMenu().findItem(R.id.gold_menu_pick_up).setVisible(false);
        toolbar.getMenu().findItem(R.id.gold_menu_pin).setVisible(false);
    }

    public void showFirstOnBoarding() {
        goldFirstOnBoarding.setVisibility(VISIBLE);
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
