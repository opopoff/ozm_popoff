package com.ozm.rocks.ui.gold.favorite;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.gold.GoldActivity;
import com.ozm.rocks.ui.gold.GoldAdapter;
import com.ozm.rocks.ui.gold.GoldComponent;
import com.ozm.rocks.ui.misc.GridInsetDecoration;
import com.ozm.rocks.util.EndlessRecyclerScrollListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldFavoriteView extends LinearLayout implements BaseView {

    @Inject
    Picasso picasso;

    @Inject
    LikeHideResult mLikeHideResult;

    @Inject
    GoldActivity.Presenter parentPresenter;

    @Inject
    GoldFavoritePresenter presenter;

    @InjectView(R.id.gold_favorite_grid_view)
    protected RecyclerView gridView;

    @InjectView(R.id.loading_more_progress)
    protected View loadingMoreProgress;

    private GoldAdapter gridAdapter;
    private final EndlessRecyclerScrollListener endlessScrollListener;
    private final StaggeredGridLayoutManager layoutManager;

    public GoldFavoriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            GoldComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }

        layoutManager = new StaggeredGridLayoutManager(
                getContext().getResources().getInteger(R.integer.column_count),
                StaggeredGridLayoutManager.VERTICAL);

        gridAdapter = new GoldAdapter(context, picasso,
                new GoldAdapter.Callback() {
                    @Override
                    public void click(final int position) {
                        parentPresenter.openShareScreen(gridAdapter.getItem(position));
                    }
                }
        );
        endlessScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            protected void onLoadMore(int page, int totalItemsCount) {
                presenter.loadFeed(page);
            }

            @Override
            protected View getProgressView() {
                return loadingMoreProgress;
            }
        };
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        gridView.setLayoutManager(layoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());
        gridView.addItemDecoration(new GridInsetDecoration(getContext(), R.dimen.grid_inset));
        gridView.setAdapter(gridAdapter);
        gridView.addOnScrollListener(endlessScrollListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }

    public void updateFeed(List<ImageResponse> imageList) {
        if (imageList.size() == 0) {
            endlessScrollListener.setIsEnd();
        } else {
            gridAdapter.addAll(imageList);
        }
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
