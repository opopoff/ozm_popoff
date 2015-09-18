package com.umad.wat.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public abstract class EndlessRecyclerScrollListener extends RecyclerView.OnScrollListener {

    private static final int LAYOUT_MANAGER_LINNEAR = 1;
    private static final int LAYOUT_MANAGER_GRID = 2;
    private static final int LAYOUT_MANAGER_STAGGERED = 3;

    private static final long DURATION_OF_ANIMATION = 200L;
    private static final int DEFAULT_VISIBLE_THRESHOLD = 10;
    private static final int DEFAULT_START_PAGE = 0;


    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold;
    // The current offset index of data you have loaded
    private int currentPage;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex;
    // If true, then no need check loading;
    private boolean isEnd = false;

    private int layoutType = 0;

    private final RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerScrollListener(RecyclerView.LayoutManager layoutManager) {
        this(layoutManager, DEFAULT_VISIBLE_THRESHOLD);
    }

    public EndlessRecyclerScrollListener(RecyclerView.LayoutManager layoutManager, int visibleThreshold) {
        this(layoutManager, visibleThreshold, DEFAULT_START_PAGE);
    }

    public EndlessRecyclerScrollListener(RecyclerView.LayoutManager layoutManager,
                                         int visibleThreshold, int startPage) {
        this.mLayoutManager = layoutManager;
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;

        if (layoutManager instanceof LinearLayoutManager) {
            layoutType = LAYOUT_MANAGER_LINNEAR;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            layoutType = LAYOUT_MANAGER_STAGGERED;
        } else if (layoutManager instanceof GridLayoutManager) {
            layoutType = LAYOUT_MANAGER_GRID;
        }
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int[] firstVisibleItemPositions = new int[2];
        int firstVisibleItem = 0;
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            firstVisibleItem = ((StaggeredGridLayoutManager) mLayoutManager)
                    .findFirstVisibleItemPositions(firstVisibleItemPositions)[0];
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else if (mLayoutManager instanceof GridLayoutManager) {
            firstVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }

        onScrolled(firstVisibleItem, visibleItemCount, totalItemCount);

        if (isEnd || totalItemCount == 0) return;

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        // If there are no items in the list, assume that initial items are loading
        if (!loading && totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            setLoading(false);
            currentPage++;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute loadMore to fetch the data.
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            setLoading(true);
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }

    }

    public void clear() {
        loading = false;
        isEnd = false;
    }

    public void setIsEnd() {
        this.isEnd = true;
        setLoading(false);
    }

    // Defines the process for actually loading more data based on page
    protected abstract void onLoadMore(int page, int totalItemsCount);

    protected abstract View getProgressView();

    protected void onScrolled(int firstVisibleItem, int visibleItemCount, int totalItemsCount) {

    }

    private void setLoading(boolean b) {
        if (b) {
            expand(getProgressView());
        } else {
            collapse(getProgressView());
        }
    }

    private void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                if (interpolatedTime == 1) {
//                    //TODO something
//                }
                v.getLayoutParams().height = (interpolatedTime == 1) ? LinearLayout.LayoutParams.WRAP_CONTENT : (int)
                        (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(DURATION_OF_ANIMATION);
        v.startAnimation(animation);
    }

    private void collapse(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int initialHeight = v.getMeasuredHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(DURATION_OF_ANIMATION);
        v.startAnimation(animation);
    }

    public boolean getLoading() {
        return loading;
    }
}
