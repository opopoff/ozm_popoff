package com.umad.rly.ui.screen.gold.novel;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.ozm.R;
import com.umad.rly.base.ComponentFinder;
import com.umad.rly.base.mvp.BaseView;
import com.umad.rly.data.RequestResultCodes;
import com.umad.rly.data.analytics.LocalyticsController;
import com.umad.rly.data.api.response.ImageResponse;
import com.umad.rly.data.image.OzomeImageLoader;
import com.umad.rly.ui.misc.FixRecyclerView;
import com.umad.rly.ui.misc.GridInsetDecoration;
import com.umad.rly.ui.screen.gold.GoldActivity;
import com.umad.rly.ui.screen.gold.GoldComponent;
import com.umad.rly.ui.screen.gold.favorite.GoldFavoriteAdapter;
import com.umad.rly.data.SharingService;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldNovelView extends FrameLayout implements BaseView {

    @Inject
    OzomeImageLoader ozomeImageLoader;

    @Inject
    LocalyticsController localyticsController;

    @Inject
    GoldActivity.Presenter parentPresenter;

    @Inject
    GoldNovelPresenter presenter;

    @InjectView(R.id.gold_novel_grid_view)
    protected FixRecyclerView gridView;

    @InjectView(R.id.loading_more_progress)
    protected View loadingMoreProgress;

    @InjectView(R.id.gold_layout_progress)
    protected ProgressBar progressBar;

    @InjectView(R.id.gold_layout_empty)
    protected View emptyView;

    private GoldFavoriteAdapter gridAdapter;
    private final GoldNovelEndlessScrollListener endlessScrollListener;
    private final StaggeredGridLayoutManager layoutManager;

    private int firstVisibleItems;

    public GoldNovelView(Context context, AttributeSet attrs) {
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
            public void click(ImageResponse image, final int position) {
                parentPresenter.openShareScreen(gridAdapter.getItem(position), SharingService.GOLD_RANDOM);
            }

            @Override
            public void doubleTap(ImageResponse image, int position) {
                presenter.like(image);
                if (!image.liked) {
                    parentPresenter.moveItem(image);
                }
                image.liked = true;
            }
        };
        gridAdapter = new GoldFavoriteAdapter(context, ozomeImageLoader, layoutManager, callback);
        gridAdapter.setOnDecideListener(new GoldFavoriteAdapter.OnDecideListener() {
            @Override
            public void callDecide(int count) {
                localyticsController.showedNImagesInNew(parentPresenter.getCategory().description, count);
            }
        });
        endlessScrollListener = new GoldNovelEndlessScrollListener(layoutManager) {
            @Override
            protected void onFirstVisibleItemCount(final int count) {
                firstVisibleItems = count;
            }

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
        gridView.addItemDecoration(new GridInsetDecoration(getContext(), R.dimen.staggered_grid_inset));
        gridView.setAdapter(gridAdapter);
        gridView.addOnScrollListener(endlessScrollListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout header = (FrameLayout) inflater.inflate(
                R.layout.gold_novel_header, null, false);
        gridAdapter.addHeader(header);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }

    public void updateFeed(List<ImageResponse> imageList) {
        if (imageList.size() == 0) {
            endlessScrollListener.setIsEnd();
            emptyView.setVisibility(VISIBLE);
        } else {
            emptyView.setVisibility(GONE);
            gridAdapter.addAll(imageList);
        }
        progressBar.setVisibility(GONE);
    }

    public void showView() {
        if (firstVisibleItems > 0) {
            localyticsController.showedNImagesInNew(parentPresenter.getCategory().description, firstVisibleItems);
            firstVisibleItems = 0;
        }
    }


    public void hideImage(ImageResponse imageResponse) {
        gridAdapter.deleteChild(imageResponse);
    }

    public void likeImage(ImageResponse imageResponse, int resultCode) {
        int position = gridAdapter.indexOf(imageResponse);
        if (resultCode == RequestResultCodes.RESULT_CODE_LIKE_IMAGE) {
            imageResponse.liked = !imageResponse.liked;
        } else if (resultCode == RequestResultCodes.RESULT_CODE_SHARE_IMAGE) {
            imageResponse.shared = !imageResponse.shared;
        }
        if (position != -1) {
            gridAdapter.set(position, imageResponse);
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
