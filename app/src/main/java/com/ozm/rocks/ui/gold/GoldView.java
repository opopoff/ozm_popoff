package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.EndlessScrollListener;
import com.ozm.rocks.util.NetworkState;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldView extends LinearLayout implements BaseView {
    public static final int DIFF_GRID_POSITION = 50;

    @Inject
    GoldActivity.Presenter presenter;
    @Inject
    NetworkState mNetworkState;

    @InjectView(R.id.gold_grid_view)
    StaggeredGridView staggeredGridView;
    @InjectView(R.id.ozome_toolbar)
    OzomeToolbar toolbar;
    @InjectView(R.id.no_internet_view)
    LinearLayout noInternetView;
    @InjectView(R.id.loading_more_progress)
    View loadingMoreProgress;

    private GoldAdapter goldAdapter;
    private int mLastToFeedListPosition;
    private int mLastFromFeedListPosition;
    private final EndlessScrollListener endlessScrollListener;

    public GoldView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            GoldComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        goldAdapter = new GoldAdapter(context);
        endlessScrollListener = new EndlessScrollListener() {
            @Override
            protected void loadMore() {
                presenter.loadFeed(mLastFromFeedListPosition += DIFF_GRID_POSITION,
                        mLastToFeedListPosition += DIFF_GRID_POSITION);
            }

            @Override
            protected View getProgressView() {
                return loadingMoreProgress;
            }
        };

        mLastFromFeedListPosition = 0;
        mLastToFeedListPosition = DIFF_GRID_POSITION;
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
        staggeredGridView.setAdapter(goldAdapter);
        staggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.shareWithDialog(goldAdapter.getItem(position));
            }
        });
        staggeredGridView.setOnScrollListener(endlessScrollListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    public void updateFeed(List<ImageResponse> imageList) {
        goldAdapter.addAll(imageList);
        goldAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
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
