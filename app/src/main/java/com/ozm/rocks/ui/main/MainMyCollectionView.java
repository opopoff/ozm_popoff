package com.ozm.rocks.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class MainMyCollectionView extends FrameLayout {
    @Inject
    MainActivity.Presenter presenter;

    @InjectView(R.id.my_collection_grid_view)
    StaggeredGridView mStaggeredGridView;

    private MyCollectionAdapter mMyCollectionAdapter;

    public MainMyCollectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        mMyCollectionAdapter = new MyCollectionAdapter(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        mStaggeredGridView.setAdapter(mMyCollectionAdapter);
        loadFeed();
    }

    private void loadFeed() {
        presenter.loadMyCollection(new
                EndlessObserver<List<ImageResponse>>() {

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.d("Error");
                    }


                    @Override
                    public void onNext(List<ImageResponse> imageList) {
                        mMyCollectionAdapter.addAll(imageList);
                        mMyCollectionAdapter.notifyDataSetChanged();
                    }
                });
    }
}
