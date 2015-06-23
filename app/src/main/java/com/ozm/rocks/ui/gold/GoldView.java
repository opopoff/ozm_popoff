package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.misc.GridInsetDecoration;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.EndlessRecyclerScrollListener;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.Timestamp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldView extends FrameLayout implements BaseView {
    public static final int DATA_PART = 50;

    @Inject
    GoldActivity.Presenter presenter;
    @Inject
    NetworkState mNetworkState;
    @Inject
    LikeHideResult mLikeHideResult;
    @Inject
    Picasso picasso;

    @InjectView(R.id.gold_grid_view)
    protected RecyclerView gridView;
    @InjectView(R.id.ozome_toolbar)
    protected OzomeToolbar toolbar;
    @InjectView(R.id.loading_more_progress)
    protected View loadingMoreProgress;

    private GoldAdapter goldAdapter;
    private int mLastToFeedListPosition;
    private int mLastFromFeedListPosition;
    private final EndlessRecyclerScrollListener endlessScrollListener;
    private final StaggeredGridLayoutManager layoutManager;

    public GoldView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            GoldComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        layoutManager = new StaggeredGridLayoutManager(
                getContext().getResources().getInteger(R.integer.column_count),
                StaggeredGridLayoutManager.VERTICAL);

        goldAdapter = new GoldAdapter(context, picasso,
                new GoldAdapter.Callback() {
                    @Override
                    public void click(final int position) {
                        presenter.setSharingDialogHide(new SharingService.SharingDialogHide() {
                            @Override
                            public void hide() {
                                ArrayList<Action> actions = new ArrayList<>();
                                actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(
                                        goldAdapter.getItem(position).id,
                                        Timestamp.getUTC(), goldAdapter.getItem(position).categoryId));
                                postHide(new HideRequest(actions), position);
                                mLikeHideResult.hideItem(goldAdapter.getItem(position).url);
                            }
                        });
                        presenter.shareWithDialog(goldAdapter.getItem(position));
                    }
                }
        );
        endlessScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            protected void onLoadMore(int page, int totalItemsCount) {
                presenter.loadFeed(mLastFromFeedListPosition += DATA_PART,
                        mLastToFeedListPosition += DATA_PART);
            }

            @Override
            protected View getProgressView() {
                return loadingMoreProgress;
            }
        };

        mLastFromFeedListPosition = 0;
        mLastToFeedListPosition = DATA_PART;
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
        gridView.setLayoutManager(layoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());
        gridView.addItemDecoration(new GridInsetDecoration(getContext(), R.dimen.grid_inset));
        gridView.setAdapter(goldAdapter);
        gridView.addOnScrollListener(endlessScrollListener);
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

    public void hideToolbarMenu(){
        toolbar.getMenu().findItem(R.id.gold_menu_pick_up).setVisible(false);
        toolbar.getMenu().findItem(R.id.gold_menu_pin).setVisible(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    public void updateFeed(List<ImageResponse> imageList) {
        if (imageList.size() == 0) {
            endlessScrollListener.setIsEnd();
        } else {
            goldAdapter.addAll(imageList);
        }
    }

    private void postHide(HideRequest hideRequest, final int position) {
//        animateRemoval(position);
        goldAdapter.deleteChild(position);
        presenter.hide(hideRequest);
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
