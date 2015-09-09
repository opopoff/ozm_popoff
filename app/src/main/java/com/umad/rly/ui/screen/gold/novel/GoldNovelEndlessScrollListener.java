package com.umad.rly.ui.screen.gold.novel;

import android.support.v7.widget.StaggeredGridLayoutManager;

import com.umad.rly.util.EndlessRecyclerScrollListener;

public abstract class GoldNovelEndlessScrollListener extends EndlessRecyclerScrollListener {

    private boolean flg = false;

    public GoldNovelEndlessScrollListener(StaggeredGridLayoutManager layoutManager) {
        super(layoutManager);
    }

    public GoldNovelEndlessScrollListener(StaggeredGridLayoutManager layoutManager, int visibleThreshold) {
        super(layoutManager, visibleThreshold);
    }

    public GoldNovelEndlessScrollListener(StaggeredGridLayoutManager layoutManager, int visibleThreshold, int startPage) {
        super(layoutManager, visibleThreshold, startPage);
    }

    @Override
    protected void onScrolled(int firstVisibleItem, int visibleItemCount, int totalItemsCount) {
        super.onScrolled(firstVisibleItem, visibleItemCount, totalItemsCount);
        if (!flg && visibleItemCount > 0) {
            flg = true;
            onFirstVisibleItemCount(visibleItemCount);
        }
    }

    protected abstract void onFirstVisibleItemCount(int count);
}
