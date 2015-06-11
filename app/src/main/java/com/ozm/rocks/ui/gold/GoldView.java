package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.misc.StaggeredGridViewWithCallback;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.EndlessScrollListener;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.Timestamp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldView extends FrameLayout implements BaseView, ObservableScrollViewCallbacks {
    public static final int DIFF_GRID_POSITION = 50;
    public static final long DURATION_DELETE_ANIMATION = 300;

    @Inject
    GoldActivity.Presenter presenter;
    @Inject
    NetworkState mNetworkState;
    @Inject
    LikeHideResult mLikeHideResult;
    @Inject
    Picasso picasso;

    @InjectView(R.id.gold_grid_view)
    StaggeredGridViewWithCallback staggeredGridView;
    @InjectView(R.id.ozome_toolbar)
    OzomeToolbar toolbar;
    @InjectView(R.id.loading_more_progress)
    View loadingMoreProgress;
    @InjectView(R.id.first_fresh_image)
    ImageView firstFreshImage;
    @InjectView(R.id.second_fresh_image)
    ImageView secondFreshImage;
    @InjectView(R.id.third_fresh_image)
    ImageView thirdFreshImage;
    @InjectView(R.id.fresh)
    LinearLayout fresh;

    private GoldAdapter goldAdapter;
    private int mLastToFeedListPosition;
    private int mLastFromFeedListPosition;
    private Context context;
    private final EndlessScrollListener endlessScrollListener;
    private Map<Long, Integer> mItemIdTopMap = new HashMap<>();

    public GoldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode()) {
            GoldComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        goldAdapter = new GoldAdapter(context, picasso);
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
        staggeredGridView.setScrollViewCallbacks(this);
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
        if (imageList.get(0).mainColor != null) {
            firstFreshImage.setBackgroundColor(Color.parseColor("#" + imageList.get(0).mainColor));
        }
        picasso.load(imageList.get(0).url).noFade().into(firstFreshImage, null);
        if (imageList.get(1).mainColor != null) {
            secondFreshImage.setBackgroundColor(Color.parseColor("#" + imageList.get(1).mainColor));
        }
        picasso.load(imageList.get(1).url).noFade().into(secondFreshImage, null);
        if (imageList.get(2).mainColor != null) {
            thirdFreshImage.setBackgroundColor(Color.parseColor("#" + imageList.get(2).mainColor));
        }
        picasso.load(imageList.get(2).url).noFade().into(thirdFreshImage, null);
        goldAdapter.addAll(imageList);
        goldAdapter.notifyDataSetChanged();
    }

    private void postHide(HideRequest hideRequest, final int positionInList) {
        animateRemoval(positionInList);
        presenter.hide(hideRequest);
    }

    public void clearAdapter() {
        goldAdapter.clear();
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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewHelper.setTranslationY(fresh, -scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
