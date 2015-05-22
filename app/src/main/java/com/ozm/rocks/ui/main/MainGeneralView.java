package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.util.EndlessScrollListener;
import com.ozm.rocks.util.NetworkState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainGeneralView extends LinearLayout {
    public static final int DIFF_LIST_POSITION = 50;
    public static final long DURATION_DELETE_ANIMATION = 300;


    @Inject
    MainActivity.Presenter presenter;

    @Inject
    KeyboardPresenter keyboardPresenter;

    @Inject
    NetworkState mNetworkState;

    private final GeneralListAdapter listAdapter;
    private final EndlessScrollListener mEndlessScrollListener;
    private int mLastToFeedListPosition;
    private int mLastFromFeedListPosition;
    private Map<Long, Integer> mItemIdTopMap = new HashMap<>();


    @InjectView(R.id.general_list_view)
    ListView generalListView;
    @InjectView(R.id.general_loading_more_progress)
    View loadingMoreProgress;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    public MainGeneralView(Context context, AttributeSet attrs) {
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
                presenter.saveImage(imageResponse.url);
            }

            @Override
            public void dislike(int position, DislikeRequest dislikeRequest, ImageResponse image) {
                postDislike(dislikeRequest, position);
                presenter.deleteImage(image);
            }

            @Override
            public void share(ImageResponse image) {
                presenter.shareWithDialog(image);
            }

            @Override
            public void hide(int position, HideRequest hideRequest) {
//                postHide(hideRequest, position);
            }

            @Override
            public void openCategory(long categoryId, String categoryName) {
                presenter.openOneEmotionScreen(categoryId, categoryName);
            }
        });
        initDefaultListPositions();

        mNetworkState.addConnectedListener(new NetworkState.IConnected() {
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

    //    @InjectView(R.id.groupon_toolbar)
//    OzomeToolbar toolbar;


//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        ArrayList<PInfo> packages = presenter.getPackages();
//        toolbar.setTitleVisibility(false);
//        toolbar.setLogoVisibility(true);

//    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

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

        generalListView.setAdapter(listAdapter);

        loadFeed(mLastFromFeedListPosition, mLastToFeedListPosition);

//        merlin.registerConnectable(new Connectable() {
//            @Override
//            public void onConnect() {
//                int i = 0;
//            }
//        });
//        merlin.registerDisconnectable(new Disconnectable() {
//            @Override
//            public void onDisconnect() {
//                int i = 0;
//            }
//        });
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
        presenter.like(likeRequest, new
                EndlessObserver<String>() {
                    @Override
                    public void onNext(String response) {
                        listAdapter.updateLikedItem(positionInList, true);
                    }
                });
    }

    private void postDislike(DislikeRequest dislikeRequest, final int positionInList) {
        presenter.dislike(dislikeRequest, new
                EndlessObserver<String>() {
                    @Override
                    public void onNext(String response) {
                        listAdapter.updateLikedItem(positionInList, false);
                    }
                });
    }

//    private void postHide(HideRequest hideRequest, final int positionInList) {
//        presenter.hide(hideRequest, new
//                EndlessObserver<String>() {
//                    @Override
//                    public void onNext(String response) {
//                        animateRemoval(positionInList);
//                    }
//                });
//    }


    @Override
    protected void onDetachedFromWindow() {
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
//                                    child.animate().withEndAction(new Runnable()
//                                    {
//                                        @Override
//                                        public void run()
//                                        {
//
//                                        }
//                                    });
                                firstAnimation = true;
                            }
                        }
                    } else {
//                        if (startTop == null) {
//                            int childHeight = child.getHeight() + generalListView.getDividerHeight();
//                            startTop = top + (i > 0 ? childHeight : -childHeight);
//                        }
                        int childHeight = child.getHeight() + generalListView.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(DURATION_DELETE_ANIMATION).translationY(0);
                        if (firstAnimation) {
//                                    child.animate().withEndAction(new Runnable()
//                                    {
//                                        @Override
//                                        public void run()
//                                        {
//
//                                        }
//                                    });
                            firstAnimation = false;
                        }
                    }

                }
                mItemIdTopMap.clear();
                return true;
            }
        });

    }
}
