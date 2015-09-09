package com.umad.rly.ui.screen.gold.favorite;

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
import com.umad.rly.data.SharingService;
import com.umad.rly.data.api.response.Category;
import com.umad.rly.data.api.response.ImageResponse;
import com.umad.rly.data.image.OzomeImageLoader;
import com.umad.rly.ui.misc.FixRecyclerView;
import com.umad.rly.ui.misc.GridInsetDecoration;
import com.umad.rly.ui.screen.gold.GoldActivity;
import com.umad.rly.ui.screen.gold.GoldComponent;
import com.umad.rly.util.EndlessRecyclerScrollListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldFavoriteView extends FrameLayout implements BaseView {

    @Inject
    OzomeImageLoader ozomeImageLoader;

    @Inject
    GoldActivity.Presenter parentPresenter;

    @Inject
    GoldFavoritePresenter presenter;

    @InjectView(R.id.gold_favorite_grid_view)
    protected FixRecyclerView gridView;

    @InjectView(R.id.gold_layout_progress)
    protected ProgressBar progressBar;

    @InjectView(R.id.loading_more_progress)
    protected View loadingMoreProgress;

    @InjectView(R.id.gold_layout_empty)
    protected View emptyView;

    private GoldFavoriteAdapter gridAdapter;
    private final EndlessRecyclerScrollListener endlessScrollListener;
    private final StaggeredGridLayoutManager layoutManager;
    private GoldFavoriteHeaderView header;

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
                parentPresenter.openShareScreen(image, SharingService.GOLD_FAVORITES);
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
            }
        };
        gridAdapter = new GoldFavoriteAdapter(context, ozomeImageLoader, layoutManager, callback);
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
        final Category category = parentPresenter.getCategory();
        if (category.isPromo) {
            header = (GoldFavoriteHeaderView) LayoutInflater.from(getContext()).inflate(
                    R.layout.gold_favorite_header_view, null, false);
            header.bindData(category);
            header.seOnSaveButtonLickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentPresenter.pin();
                    header.setVisibility(View.GONE);
                }
            });
            gridAdapter.addHeader(header);
        }  else if (header != null) {
            gridAdapter.removeHeader(header);
            header = null;
        }
        gridView.setLayoutManager(layoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());
        gridView.addItemDecoration(new GridInsetDecoration(getContext(), R.dimen.staggered_grid_inset));
        gridView.setAdapter(gridAdapter);
        gridView.addOnScrollListener(endlessScrollListener);

    }

    public void hideImage(ImageResponse imageResponse) {
        gridAdapter.deleteChild(imageResponse);
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
            emptyView.setVisibility(VISIBLE);
        } else {
            emptyView.setVisibility(GONE);
            gridAdapter.addAll(imageList);
        }
        progressBar.setVisibility(GONE);
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
        gridAdapter.notifyDataSetChanged();
    }

    public void likeShareImage(ImageResponse imageResponse, int resultCode) {
        int position = gridAdapter.indexOf(imageResponse);
        if (resultCode == RequestResultCodes.RESULT_CODE_LIKE_IMAGE) {
            imageResponse.liked = !imageResponse.liked;
        } else if (resultCode == RequestResultCodes.RESULT_CODE_SHARE_IMAGE) {
            imageResponse.shared = !imageResponse.shared;
        }
        if (position != -1) {
            gridAdapter.set(position, imageResponse);
        } else {
            addResourceImage(imageResponse);
        }
    }
}
