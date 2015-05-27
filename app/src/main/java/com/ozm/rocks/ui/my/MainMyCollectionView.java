package com.ozm.rocks.ui.my;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class MainMyCollectionView extends FrameLayout implements BaseView {
    @Inject
    MainActivity.Presenter presenter;
    @Inject
    MainMyCollectionPresenter myPresenter;

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
        mStaggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myPresenter.shareWithDialog(mMyCollectionAdapter.getItem(position));
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        myPresenter.takeView(this);
    }

    public void loadFeed() {
        presenter.loadMyCollection(
                new EndlessObserver<List<ImageResponse>>() {

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.d("Error");
                    }


                    @Override
                    public void onNext(List<ImageResponse> imageList) {
                        mMyCollectionAdapter.clear();
                        mMyCollectionAdapter.addAll(imageList);
                        mMyCollectionAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDetachedFromWindow() {
        myPresenter.dropView(this);
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
