package com.umad.rly.ui.screen.main.general;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.ozm.R;
import com.umad.rly.base.ComponentFinder;
import com.umad.rly.base.mvp.BaseView;
import com.umad.rly.base.tools.KeyboardPresenter;
import com.umad.rly.data.analytics.LocalyticsController;
import com.umad.rly.data.api.request.DislikeRequest;
import com.umad.rly.data.api.request.LikeRequest;
import com.umad.rly.data.api.response.CategoryResponse;
import com.umad.rly.data.api.response.ImageResponse;
import com.umad.rly.data.rx.EndlessObserver;
import com.umad.rly.ui.misc.BetterViewAnimator;
import com.umad.rly.ui.screen.main.MainActivity;
import com.umad.rly.ui.screen.main.MainComponent;
import com.umad.rly.util.EndlessScrollListener;
import com.umad.rly.util.NetworkState;
import com.umad.rly.util.PInfo;
import com.squareup.picasso.Picasso;

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
    public static final long DURATION_ONBOARDING_ANIMATION = 500;
    public static final long DURATION_LIKE_ANIMATION = 500;
    public static final long DURATION_HIDE_DELAY_LIKE_ANIMATION = 1000;

    @Inject
    MainActivity.Presenter presenter;
    @Inject
    GeneralPresenter generalPresenter;
    @Inject
    KeyboardPresenter keyboardPresenter;
    @Inject
    Picasso picasso;
    @Inject
    LocalyticsController localyticsController;

    @Inject
    NetworkState mNetworkState;

    private final GeneralAdapter listAdapter;
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
    //    @InjectView(R.id.main_general_filter_container)
//    protected FilterView filterContainer;
    @InjectView(R.id.main_general_filter_list_view)
    protected ListView categoryListView;
    @InjectView(R.id.main_general_better_view_amimator)
    protected BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.general_on_boarding_message)
    protected FrameLayout onBoardingMessage;
    @InjectView(R.id.general_like_text)
    protected TextView likeTextView;

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
            protected void onLoadMore(int page, int totalItemsCount) {
                loadFeed(mLastFromFeedListPosition += DIFF_LIST_POSITION,
                        mLastToFeedListPosition += DIFF_LIST_POSITION);
            }

            @Override
            protected View getProgressView() {
                return loadingMoreProgress;
            }
        };

        listAdapter = new GeneralAdapter(context, new GeneralAdapter.ActionListener() {
            @Override
            public void like(int position, LikeRequest likeRequest, ImageResponse image) {
                image.liked = false;
                generalPresenter.likeDislike(image);
                showLikeMessage(image);
            }

            @Override
            public void dislike(int position, DislikeRequest dislikeRequest, ImageResponse image) {
                image.liked = true;
                generalPresenter.likeDislike(image);
            }

            @Override
            public void share(final ImageResponse image, final int position) {
                generalPresenter.shareWithDialog(image);
            }

            @Override
            public void clickByCategory(long categoryId, String categoryName) {
                localyticsController.openFeed(LocalyticsController.WIZARD);
                selectFilterItemById(categoryId);
            }

            @Override
            public void fastShare(PInfo pInfo, ImageResponse image) {
                localyticsController.shareOutside(pInfo.getApplicationName());
                generalPresenter.fastSharing(pInfo, image);
            }

            @Override
            public void onBoarding() {
                generalPresenter.onBoarding();
            }

            @Override
            public void newMaximumShowedDecide(int decide) {
                localyticsController.showedNImagesInFeed(decide);
            }
        }, picasso);
        initDefaultListPositions();
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

//        removeView(header);
//        header.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater layoutInflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = layoutInflater.inflate(R.layout.main_general_header, generalListView, false);
        generalListView.addHeaderView(header, null, false);
        View footer = layoutInflater.inflate(R.layout.main_general_footer, generalListView, false);
        generalListView.addFooterView(footer, null, false);
        generalListView.setAdapter(listAdapter);

        loadFeed(mLastFromFeedListPosition, mLastToFeedListPosition);

//        filterContainer.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!filterContainer.isChecked()) {
//                    showFilter();
//                } else {
//                    showContent();
//                }
//            }
//        });
//
//        categoryListAdapter = new FilterListAdapter(getContext());
//        categoryListView.setAdapter(categoryListAdapter);
//        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectFilterItemById(id);
//            }
//        });
    }

    private void selectFilterItemById(long id) {
        final FilterListItemData item = categoryListAdapter.getItemById(id);
        if (item == null) return;
//        filterContainer.setTitle(item.title);
        localyticsController.openFilter(item.title);
        listAdapter.setFilter(item.id == FilterListAdapter.DEFAULT_ITEM_IT
                ? GeneralAdapter.FILTER_CLEAN_STATE : item.id);
        showContent();
        postDelayed(new Runnable() {
            @Override
            public void run() {
//                generalListView.setSelection(0);
                categoryListView.setSelection(0);
            }
        }, 250);
    }

    private void loadFeed(int lastFromFeedListPosition, int lastToFeedListPosition) {
        generalPresenter.loadGeneralFeed(lastFromFeedListPosition, lastToFeedListPosition,
                new EndlessObserver<List<ImageResponse>>() {
                    @Override
                    public void onError(Throwable throwable) {
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
        generalPresenter.updateGeneralFeed(mLastFromFeedListPosition, mLastToFeedListPosition, new
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

    @Override
    protected void onDetachedFromWindow() {
        generalPresenter.dropView(this);
        hideOnBoardingMessage();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public GeneralAdapter getListAdapter() {
        return listAdapter;
    }

    private void animateRemoval(int position) {
//        View viewToRemove = generalListView.getChildAt(position);
//        int firstVisiblePosition = generalListView.getFirstVisiblePosition();
//        for (int i = 0; i < generalListView.getChildCount(); ++i) {
//            View child = generalListView.getChildAt(i);
//            if (child != viewToRemove) {
//                int positionView = firstVisiblePosition + i;
//                long itemId = listAdapter.getItemId(positionView);
//                mItemIdTopMap.put(itemId, child.getTop());
//            }
//        }
        listAdapter.deleteChild(position);

//        final ViewTreeObserver observer = generalListView.getViewTreeObserver();
//        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                observer.removeOnPreDrawListener(this);
//                boolean firstAnimation = true;
//                int firstVisiblePosition = generalListView.getFirstVisiblePosition();
//                for (int i = 0; i < generalListView.getChildCount(); ++i) {
//                    final View child = generalListView.getChildAt(i);
//                    int position = firstVisiblePosition + i;
//                    long itemId = listAdapter.getItemId(position);
//                    Integer startTop = mItemIdTopMap.get(itemId);
//                    int top = child.getTop();
//                    if (startTop != null) {
//                        if (startTop != top) {
//                            int delta = startTop - top;
//                            child.setTranslationY(delta);
//                            child.animate().setDuration(DURATION_DELETE_ANIMATION).translationY(0);
//                            if (firstAnimation) {
//                                firstAnimation = true;
//                            }
//                        }
//                    } else {
//                        int childHeight = child.getHeight() + generalListView.getDividerHeight();
//                        startTop = top + (i > 0 ? childHeight : -childHeight);
//                        int delta = startTop - top;
//                        child.setTranslationY(delta);
//                        child.animate().setDuration(DURATION_DELETE_ANIMATION).translationY(0);
//                        if (firstAnimation) {
//                            firstAnimation = false;
//                        }
//                    }
//
//                }
//                mItemIdTopMap.clear();
//                return true;
//            }
//        });

    }

    public void loadFeedFromNetworkState(boolean isConnected) {
        if (isConnected && (mEndlessScrollListener.getLoading() || listAdapter.getCount() == 0)) {
            loadFeed(mLastFromFeedListPosition, mLastToFeedListPosition);
        }
    }

    public void showOnBoardingMessage() {
//        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
//        alphaAnimation.setDuration(DURATION_ONBOARDING_ANIMATION);
//        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                onBoardingMessage.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        onBoardingMessage.startAnimation(alphaAnimation);
    }

    public void hideOnBoardingMessage() {
//        if (onBoardingMessage.getVisibility() == VISIBLE) {
//            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
//            alphaAnimation.setDuration(DURATION_ONBOARDING_ANIMATION);
//            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    onBoardingMessage.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                }
//            });
//            onBoardingMessage.startAnimation(alphaAnimation);
//        }
    }

    public void showLikeMessage(final ImageResponse imageResponse) {
//        likeTextView.setText(getResources().getString(R.string
//                .general_like_message, imageResponse.categoryDescription));
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation1.setDuration(DURATION_LIKE_ANIMATION);
        ((View) likeTextView.getParent()).setVisibility(View.VISIBLE);
        alphaAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((View) likeTextView.getParent()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                        alphaAnimation.setDuration(DURATION_LIKE_ANIMATION);
                        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                ((View) likeTextView.getParent()).setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        ((View) likeTextView.getParent()).startAnimation(alphaAnimation);
                    }
                }, DURATION_HIDE_DELAY_LIKE_ANIMATION);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ((View) likeTextView.getParent()).startAnimation(alphaAnimation1);
    }


    @Override
    public void showLoading() {
        betterViewAnimator.setDisplayedChildId(R.id.general_loading_view);
    }

    @Override
    public void showContent() {
//        filterContainer.setChecked(false);
        betterViewAnimator.setDisplayedChildId(R.id.main_general_image_list_container);
    }

    @Override
    public void showError(Throwable throwable) {

    }

//    public void showFilter() {
//        filterContainer.setChecked(true);
//        betterViewAnimator.setDisplayedChildId(R.id.main_general_filter_list_view);
//    }

    public void bindCategory(CategoryResponse category) {
//        categoryListAdapter.addAll(FilterListItemData.from(category.categories));
//        categoryListView.setAdapter(categoryListAdapter);
    }
}
