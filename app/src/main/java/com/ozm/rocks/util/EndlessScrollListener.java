package com.ozm.rocks.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import timber.log.Timber;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private static final long DURATION_OF_ANIMATION = 200;
    private boolean mFeedLoading;
    private boolean mIsEnd;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount != 0 && !mFeedLoading && !mIsEnd) {
            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

            if (loadMore) {
                setLoading(true, false);
                loadMore();
            }
        }
        Timber.i("item number is " + firstVisibleItem);
    }

    protected abstract void loadMore();

    protected abstract View getProgressView();

    public void setLoading(boolean b, boolean isEnd) {
        mFeedLoading = b;
        mIsEnd = isEnd;
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
        return mFeedLoading;
    }
}