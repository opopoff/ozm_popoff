package com.ozm.rocks.ui.gold.favorite;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.gold.GoldActivity;
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

    private GoldFavoriteAdapter gridAdapter;
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

        final GoldFavoriteAdapter.Callback callback = new GoldFavoriteAdapter.Callback() {
            @Override
            public void click(ImageResponse image, int position) {
                parentPresenter.openShareScreen(image);
            }

            @Override
            public void doubleTap(ImageResponse image, int position) {
                final ImageResponse item = gridAdapter.getItem(position);
                if (item.liked && position == 0) {
                    return;
                }
                item.liked = false;
                presenter.like(item);
                item.liked = true;
                gridAdapter.moveChildToTop(position);
            }
        };
        gridAdapter = new GoldFavoriteAdapter(context, picasso, layoutManager, callback);
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
        if (parentPresenter.getCategory().isPromo) {
            gridAdapter.addHeader(LayoutInflater.from(getContext()).inflate(
                    R.layout.gold_favorite_header_view, null, false));
        }
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

    public void addResourceImage(ImageResponse image) {
        gridAdapter.add(0, image);
    }
}
