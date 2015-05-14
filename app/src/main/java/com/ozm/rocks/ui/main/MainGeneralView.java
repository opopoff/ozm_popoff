package com.ozm.rocks.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.tools.KeyboardPresenter;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;

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
    private int mLastToFeedListPosition;
    private int mLastFromFeedListPosition;
    private final EndlessScrollListener mEndlessScrollListener;

    public MainGeneralView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }

        mEndlessScrollListener = new EndlessScrollListener() {
            @Override
            void loadMore() {
                loadFeed(mLastFromFeedListPosition += DIFF_LIST_POSITION,
                        mLastToFeedListPosition += DIFF_LIST_POSITION);
            }
        };

        listAdapter = new GeneralListAdapter(context);
        mLastFromFeedListPosition = 0;
        mLastToFeedListPosition = 50;
    }

    //    @InjectView(R.id.groupon_toolbar)
//    OzomeToolbar toolbar;
    @InjectView(R.id.general_list_view)
    ListView generalListView;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        ArrayList<PInfo> packages = presenter.getPackages();
//        toolbar.setTitleVisibility(false);
//        toolbar.setLogoVisibility(true);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        generalListView.setOnScrollListener(mEndlessScrollListener);

        generalListView.setAdapter(listAdapter);

        loadFeed(mLastFromFeedListPosition, mLastToFeedListPosition);
    }

    private void loadFeed(int mLastFromFeedListPosition, int mLastToFeedListPosition) {
        presenter.loadGeneralFeed(mLastFromFeedListPosition, mLastToFeedListPosition, new
                EndlessObserver<List<ImageResponse>>() {

                    @Override
                    public void onNext(List<ImageResponse> imageList) {
                        listAdapter.addAll(imageList);
                    }

                    @Override
                    public void onCompleted() {
                        mEndlessScrollListener.setLoading(false);
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

    public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

        private boolean mFeedLoading;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (totalItemCount != 0 && !mFeedLoading) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

                if (loadMore) {
                    mFeedLoading = true;
                    loadMore();
                }
            }
        }

        abstract void loadMore();

        public void setLoading(boolean b) {
            mFeedLoading = b;
        }
    }
}
