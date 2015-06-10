package com.ozm.rocks.ui.general;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;
import com.ozm.rocks.ui.misc.BetterViewAnimator;
import com.ozm.rocks.ui.sharing.SharingService;
import com.ozm.rocks.util.EndlessScrollListener;
import com.ozm.rocks.util.NetworkState;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.Timestamp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class GeneralView extends FrameLayout implements BaseView {
    public static final int DIFF_LIST_POSITION = 50;
    public static final long DURATION_DELETE_ANIMATION = 300;
    private static final String KEY_LISTENER = "MainGeneralView";

    @Inject
    MainActivity.Presenter presenter;
    @Inject
    GeneralPresenter generalPresenter;
    @Inject
    KeyboardPresenter keyboardPresenter;
    @Inject
    Picasso picasso;

    @Inject
    NetworkState mNetworkState;

    private final GeneralListAdapter listAdapter;
    private final EndlessScrollListener mEndlessScrollListener;
    private int mLastToFeedListPosition;
    private int mLastFromFeedListPosition;
    private Map<Long, Integer> mItemIdTopMap = new HashMap<>();

    private FilterListAdapter categoryListAdapter;

    @InjectView(R.id.general_list_view)
    protected ObservableListView generalListView;
    @InjectView(R.id.general_loading_more_progress)
    protected View loadingMoreProgress;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.main_general_filter_container)
    protected FilterView filterContainer;
    @InjectView(R.id.main_general_filter_list_view)
    protected ListView categoryListView;
    @InjectView(R.id.main_general_better_view_amimator)
    protected BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.general_on_boarding_message)
    protected FrameLayout onBoardingMessage;

    @OnClick(R.id.general_on_boarding_cross)
    protected void click_cross() {
        hideOnBoardingMessage();
    }

    public GeneralView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }

        mEndlessScrollListener = new EndlessScrollListener() {
            @Override
            protected void loadMore() {
                loadFeed(mLastFromFeedListPosition += DIFF_LIST_POSITION,
                        mLastToFeedListPosition += DIFF_LIST_POSITION);
            }

            @Override
            protected View getProgressView() {
                return loadingMoreProgress;
            }
        };

        listAdapter = new GeneralListAdapter(context, new GeneralListAdapter.ActionListener() {
            @Override
            public void like(int position, LikeRequest likeRequest, ImageResponse imageResponse) {
                postLike(likeRequest, position);
                presenter.saveImage(imageResponse.url, imageResponse.sharingUrl);
            }

            @Override
            public void dislike(int position, DislikeRequest dislikeRequest, ImageResponse image) {
                postDislike(dislikeRequest, position);
                presenter.deleteImage(image);
            }

            @Override
            public void share(final ImageResponse image, final int position) {
                presenter.setSharingDialogHide(new SharingService.SharingDialogHide() {
                    @Override
                    public void hide() {
                        ArrayList<Action> actions = new ArrayList<>();
                        actions.add(Action.getLikeDislikeHideActionForMainFeed(image.id, Timestamp.getUTC()));
                        postHide(new HideRequest(actions), position);
                    }
                });
                generalPresenter.shareWithDialog(image);
            }

            @Override
            public void clickByCategory(long categoryId, String categoryName) {
                selectFilterItemById(categoryId);
            }

            @Override
            public void fastShare(PInfo pInfo, ImageResponse image) {
                generalPresenter.fastSharing(pInfo, image);
            }

            @Override
            public void onBoarding() {
                generalPresenter.onBoarding();
            }
        }, picasso);
        initDefaultListPositions();

        mNetworkState.addConnectedListener(KEY_LISTENER, new NetworkState.IConnected() {
            @Override
            public void connectedState(boolean isConnected) {
                if (isConnected && (mEndlessScrollListener.getLoading() || listAdapter.getCount() == 0)) {
                    loadFeed(mLastFromFeedListPosition, mLastToFeedListPosition);
                }
            }
        });
    }

    private void initDefaultListPositions() {
        mLastFromFeedListPosition = 0;
        mLastToFeedListPosition = 50;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        generalPresenter.takeView(this);


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
        generalListView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                Timber.v("ObservableScrollView: onScrollChanged: scrollY: " +
                        scrollY + " firstScroll: " + firstScroll + " dragging: " + dragging);
            }

            @Override
            public void onDownMotionEvent() {
                Timber.v("ObservableScrollView: onDownMotionEvent");
            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                Timber.v("ObservableScrollView: onUpOrCancelMotionEvent: scrollState: " + scrollState);
            }
        });

        removeView(loadingMoreProgress);
        loadingMoreProgress.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        generalListView.addFooterView(loadingMoreProgress, null, false);
        generalListView.setAdapter(listAdapter);

        loadFeed(mLastFromFeedListPosition, mLastToFeedListPosition);

        filterContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filterContainer.isChecked()) {
                    showFilter();
                } else {
                    showContent();
                }
            }
        });

        categoryListAdapter = new FilterListAdapter(getContext());
        categoryListView.setAdapter(categoryListAdapter);
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectFilterItemById(id);
            }
        });
    }

    private void selectFilterItemById(long id) {
        final FilterListItemData item = categoryListAdapter.getItemById(id);
        if (item == null) return;
        filterContainer.setTitle(item.title);
        listAdapter.setFilter(item.id == FilterListAdapter.DEFAULT_ITEM_IT
                ? GeneralListAdapter.FILTER_CLEAN_STATE : item.id);
        showContent();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                generalListView.setSelection(0);
                categoryListView.setSelection(0);
            }
        }, 250);
    }

    private void loadFeed(int lastFromFeedListPosition, int lastToFeedListPosition) {
        presenter.loadGeneralFeed(lastFromFeedListPosition, lastToFeedListPosition, new
                EndlessObserver<List<ImageResponse>>() {

                    @Override
                    public void onError(Throwable throwable) {
//                        mEndlessScrollListener.setLoading(false);
                    }

                    @Override
                    public void onNext(List<ImageResponse> imageList) {
                        listAdapter.addAll(imageList);
                        mEndlessScrollListener.setLoading(false, imageList.size() == 0);
                    }
                });
    }

    private void updateFeed() {
        presenter.updateGeneralFeed(mLastFromFeedListPosition, mLastToFeedListPosition, new
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
        presenter.like(likeRequest);
    }

    private void postDislike(DislikeRequest dislikeRequest, final int positionInList) {
        presenter.dislike(dislikeRequest);
    }

    private void postHide(HideRequest hideRequest, final int positionInList) {
        animateRemoval(positionInList);
        presenter.hide(hideRequest);
    }


    @Override
    protected void onDetachedFromWindow() {
        generalPresenter.dropView(this);
        mNetworkState.deleteConnectedListener(KEY_LISTENER);
        hideOnBoardingMessage();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public GeneralListAdapter getListAdapter() {
        return listAdapter;
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

    public void showOnBoardingMessage() {
        onBoardingMessage.setVisibility(VISIBLE);
    }

    public void hideOnBoardingMessage() {
        onBoardingMessage.setVisibility(GONE);
    }

    @Override
    public void showLoading() {
        betterViewAnimator.setDisplayedChildId(R.id.general_loading_view);
    }

    @Override
    public void showContent() {
        filterContainer.setChecked(false);
        betterViewAnimator.setDisplayedChildId(R.id.main_general_image_list_container);
    }

    @Override
    public void showError(Throwable throwable) {

    }

    public void showFilter() {
        filterContainer.setChecked(true);
        betterViewAnimator.setDisplayedChildId(R.id.main_general_filter_list_view);
    }

    public void bindCategory(CategoryResponse category) {
        categoryListAdapter.addAll(FilterListItemData.from(category.categories));
        categoryListView.setAdapter(categoryListAdapter);
    }
}
