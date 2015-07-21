package com.ozm.rocks.ui.screen.categories;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ListView;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.misc.BetterViewAnimator;
import com.ozm.rocks.ui.view.OzomeToolbar;
import com.ozm.rocks.util.EndlessScrollListener;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PInfo;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OneEmotionView extends BetterViewAnimator implements BaseView {
    public static final int DIFF_LIST_POSITION = 50;
    public static final long DURATION_DELETE_ANIMATION = 300;
    private static final String KEY_LISTENER = "OneEmotionView";


    @Inject
    OneEmotionActivity.Presenter presenter;
    @Inject
    ActivityScreenSwitcher screenSwitcher;
    @Inject
    NetworkState mNetworkState;
    @Inject
    LikeHideResult mLikeHideResult;
    @Inject
    Picasso picasso;
    @Inject
    LocalyticsController localyticsController;

    private final CategoryListAdapter listAdapter;
    private final EndlessScrollListener mEndlessScrollListener;
    private int mLastToFeedListPosition;
    private int mLastFromFeedListPosition;
    private Map<Long, Integer> mItemIdTopMap = new HashMap<>();

    @InjectView(R.id.ozome_toolbar)
    OzomeToolbar toolbar;
    @InjectView(R.id.general_list_view)
    ListView generalListView;
    @InjectView(R.id.general_loading_more_progress)
    View loadingMoreProgress;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    public OneEmotionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            OneEmotionComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }

        mEndlessScrollListener = new EndlessScrollListener() {
            @Override
            protected void onLoadMore(int page, int totalItemsCount) {
                loadFeed(mLastFromFeedListPosition += DIFF_LIST_POSITION,
                        mLastToFeedListPosition += DIFF_LIST_POSITION);
            }

            @Override
            protected View getProgressView() {
                return loadingMoreProgress;
            }
        };

        listAdapter = new CategoryListAdapter(context, new CategoryListAdapter.ActionListener() {
            @Override
            public void like(int position, LikeRequest likeRequest, ImageResponse image) {
                postLike(likeRequest, position);
                mLikeHideResult.likeItem(image.url);
                presenter.saveImage(image.url, image.sharingUrl, image.imageType);
            }

            @Override
            public void dislike(int position, DislikeRequest dislikeRequest, ImageResponse image) {
                postDislike(dislikeRequest, position);
                mLikeHideResult.dislikeItem(image.url);
                presenter.deleteImage(image);
            }

            @Override
            public void share(final ImageResponse image, final int position) {
                presenter.shareWithDialog(image);
            }

            @Override
            public void hide(int position, HideRequest hideRequest, ImageResponse image) {
            }

            @Override
            public void fastShare(PInfo pInfo, ImageResponse image) {
                localyticsController.shareOutside(pInfo.getApplicationName());
                presenter.fastSharing(pInfo, image);
            }

            @Override
            public void newMaximumShowedDecide(int decide) {
                localyticsController.showedNImagesInFeed(decide);
            }

        }, picasso);
        initDefaultListPositions();
    }

    public int getLastToFeedListPosition() {
        return mLastToFeedListPosition;
    }

    public int getLastFromFeedListPosition() {
        return mLastFromFeedListPosition;
    }

    private void initDefaultListPositions() {
        mLastFromFeedListPosition = 0;
        mLastToFeedListPosition = 50;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        mNetworkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
            @Override
            public void connectedState(boolean isConnected) {
                if (isConnected && (mEndlessScrollListener.getLoading() || listAdapter.getCount() == 0)) {
                    loadFeed(mLastFromFeedListPosition, mLastToFeedListPosition);
                }
            }
        });
        toolbar.setTitleVisibility(true);
        toolbar.setLogoVisibility(false);
        toolbar.setNavigationIconVisibility(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.goBack();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initDefaultListPositions();
                updateFeed();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        generalListView.setOnScrollListener(mEndlessScrollListener);

        removeView(loadingMoreProgress);
        loadingMoreProgress.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        generalListView.addFooterView(loadingMoreProgress, null, false);
        generalListView.setAdapter(listAdapter);

//        loadFeed(mLastFromFeedListPosition, mLastToFeedListPosition);
    }

    public void loadFeed(int lastFromFeedListPosition, int lastToFeedListPosition) {
        presenter.loadCategoryFeed(lastFromFeedListPosition, lastToFeedListPosition, new
                EndlessObserver<List<ImageResponse>>() {

                    @Override
                    public void onError(Throwable throwable) {
//                        mEndlessScrollListener.setLoading(false);
                    }

                    @Override
                    public void onNext(List<ImageResponse> imageList) {
                        listAdapter.addAll(imageList);
                        if (imageList.size() == 0) {
                            mEndlessScrollListener.setIsEnd();
                        }
                    }
                });
    }

    private void updateFeed() {
        presenter.updateCategoryFeed(mLastFromFeedListPosition, mLastToFeedListPosition, new
                EndlessObserver<List<ImageResponse>>() {

                    @Override
                    public void onError(Throwable throwable) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<ImageResponse> imageList) {
                        listAdapter.updateAll(imageList);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void postLike(final LikeRequest likeRequest, final int positionInList) {
        presenter.like(likeRequest, new
                EndlessObserver<String>() {
                    @Override
                    public void onNext(String response) {
//                        listAdapter.updateLikedItem(positionInList, true);
                    }
                });
    }

    private void postDislike(DislikeRequest dislikeRequest, final int positionInList) {
        presenter.dislike(dislikeRequest, new
                EndlessObserver<String>() {
                    @Override
                    public void onNext(String response) {
//                        listAdapter.updateLikedItem(positionInList, false);
                    }
                });
    }

    private void postHide(HideRequest hideRequest, final int positionInList) {
        animateRemoval(positionInList);
        presenter.hide(hideRequest, new
                EndlessObserver<String>() {
                    @Override
                    public void onNext(String response) {
                    }
                });
    }

    @Override
    protected void onDetachedFromWindow() {
        mNetworkState.deleteConnectedListener(KEY_LISTENER);
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void animateRemoval(int position) {
        View viewToRemove = generalListView.getChildAt(position);
        int firstVisiblePosition = generalListView.getFirstVisiblePosition();
        for (int i = 0; i < generalListView.getChildCount(); ++i) {
            View child = generalListView.getChildAt(i);
            if (child != viewToRemove) {
                int positionView = firstVisiblePosition + i;
                long itemId = listAdapter.getItemId(positionView);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        listAdapter.deleteChild(position);

        final ViewTreeObserver observer = generalListView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = generalListView.getFirstVisiblePosition();
                for (int i = 0; i < generalListView.getChildCount(); ++i) {
                    final View child = generalListView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = listAdapter.getItemId(position);
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
                        int childHeight = child.getHeight() + generalListView.getDividerHeight();
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
    public void showLoading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showContent() {
        setDisplayedChildId(R.id.main_emotion_content);
    }

    @Override
    public void showError(Throwable throwable) {
        // TODO: implement no network error
    }

    public CategoryListAdapter getFeedAdapter() {
        return listAdapter;
    }
}