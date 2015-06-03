package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.EndlessScrollListener;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldView extends LinearLayout implements BaseView {
    public static final int DIFF_GRID_POSITION = 50;
    public static final long DURATION_DELETE_ANIMATION = 300;

    @Inject
    GoldActivity.Presenter presenter;
    @Inject
    NetworkState mNetworkState;
    @Inject
    LikeHideResult mLikeHideResult;

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
    private Map<Long, Integer> mItemIdTopMap = new HashMap<>();

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
                if (goldAdapter.getCount() >= DIFF_GRID_POSITION) {
                    presenter.loadFeed(mLastFromFeedListPosition += DIFF_GRID_POSITION,
                            mLastToFeedListPosition += DIFF_GRID_POSITION);
                }
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
        goldAdapter.setCallback(new GoldAdapter.Callback() {
            @Override
            public void click(final int position) {
                presenter.setSharingDialogHide(new SharingService.SharingDialogHide() {
                    @Override
                    public void hide() {
                        ArrayList<Action> actions = new ArrayList<>();
                        actions.add(Action.getLikeDislikeHideActionForGoldenPersonal(goldAdapter.getItem(position).id,
                                Timestamp.getUTC(), goldAdapter.getItem(position).categoryId));
                        postHide(new HideRequest(actions), position);
                        mLikeHideResult.hideItem(goldAdapter.getItem(position).url);
                    }
                });
                presenter.shareWithDialog(goldAdapter.getItem(position));
            }
        });
        staggeredGridView.setOnScrollListener(endlessScrollListener);
    }

    private void animateRemoval(int position) {
        View viewToRemove = staggeredGridView.getChildAt(position);
        int firstVisiblePosition = staggeredGridView.getFirstVisiblePosition();
        for (int i = 0; i < staggeredGridView.getChildCount(); ++i) {
            View child = staggeredGridView.getChildAt(i);
            if (child != viewToRemove) {
                int positionView = firstVisiblePosition + i;
                long itemId = goldAdapter.getItemId(positionView);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        goldAdapter.deleteChild(position);

        final ViewTreeObserver observer = staggeredGridView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = staggeredGridView.getFirstVisiblePosition();
                for (int i = 0; i < staggeredGridView.getChildCount(); ++i) {
                    final View child = staggeredGridView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = goldAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(DURATION_DELETE_ANIMATION).translationY(0);
                            if (firstAnimation) {
                                firstAnimation = true;
                            }
                        }
                    } else {
                        int childHeight = child.getHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(DURATION_DELETE_ANIMATION).translationY(0);
                        if (firstAnimation) {
                            firstAnimation = false;
                        }
                    }

                }
                mItemIdTopMap.clear();
                return true;
            }
        });

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

    private void postHide(HideRequest hideRequest, final int positionInList) {
        animateRemoval(positionInList);
        presenter.hide(hideRequest);
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
