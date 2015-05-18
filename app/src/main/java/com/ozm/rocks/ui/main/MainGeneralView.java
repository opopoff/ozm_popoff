package com.ozm.rocks.ui.main;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.util.EndlessScrollListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainGeneralView extends LinearLayout {
    public static final int DIFF_LIST_POSITION = 50;

    @Inject
    MainActivity.Presenter presenter;

    @Inject
    KeyboardPresenter keyboardPresenter;

    private final GeneralListAdapter listAdapter;
    private final EndlessScrollListener mEndlessScrollListener;
    private int mLastToFeedListPosition;
    private int mLastFromFeedListPosition;

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
                presenter.showSharingDialog(image);
            }
        });
        initDefaultListPositions();
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
    }

    private void loadFeed(int lastFromFeedListPosition, int lastToFeedListPosition) {
        presenter.loadGeneralFeed(lastFromFeedListPosition, lastToFeedListPosition, new
                EndlessObserver<List<ImageResponse>>() {

                    @Override
                    public void onError(Throwable throwable) {
                        mEndlessScrollListener.setLoading(false);
                    }

                    @Override
                    public void onNext(List<ImageResponse> imageList) {
                        listAdapter.addAll(imageList);
                        mEndlessScrollListener.setLoading(false);
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


    @Override
    protected void onDetachedFromWindow() {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public GeneralListAdapter getListAdapter() {
        return listAdapter;
    }
}
