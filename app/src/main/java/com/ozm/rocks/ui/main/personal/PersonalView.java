package com.ozm.rocks.ui.main.personal;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;
import com.ozm.rocks.ui.misc.GridInsetDecoration;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class PersonalView extends FrameLayout implements BaseView {

    @Inject
    MainActivity.Presenter presenter;
    @Inject
    PersonalPresenter myPresenter;
    @Inject
    LikeHideResult mLikeHideResult;
    @Inject
    Picasso picasso;

    @InjectView(R.id.my_collection_grid_view)
    protected RecyclerView staggeredGridView;

    private PersonalAdapter personalAdapter;

    private final StaggeredGridLayoutManager layoutManager;

    public PersonalView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        layoutManager = new StaggeredGridLayoutManager(
                getContext().getResources().getInteger(R.integer.column_count),
                StaggeredGridLayoutManager.VERTICAL);

        personalAdapter = new PersonalAdapter(context, layoutManager, picasso);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        staggeredGridView.setLayoutManager(layoutManager);
        staggeredGridView.setItemAnimator(new DefaultItemAnimator());
        staggeredGridView.addItemDecoration(new GridInsetDecoration(getContext(), R.dimen.grid_inset));
        staggeredGridView.setAdapter(personalAdapter);
        personalAdapter.setCallback(new PersonalAdapter.Callback() {
            @Override
            public void click(final int position) {
                myPresenter.openShareScreen(personalAdapter.getItem(position));
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
                        if (imageList.size() > 0) {
                            preloadImages(imageList);
                            findViewById(R.id.my_collection_empty_view).setVisibility(GONE);
                            personalAdapter.clear();
                            personalAdapter.addAll(imageList);
                            personalAdapter.notifyDataSetChanged();
                        } else {
                            findViewById(R.id.my_collection_empty_view).setVisibility(VISIBLE);
                        }
                    }
                });
    }

    private void preloadImages(List<ImageResponse> imageList) {
        for (ImageResponse imageResponse : imageList) {
            Ion.with(getContext()).load(imageResponse.url).withBitmap().asBitmap();
        }
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
