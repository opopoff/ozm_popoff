package com.ozm.rocks.ui.personal;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.rx.EndlessObserver;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;
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
    StaggeredGridView staggeredGridView;

    private PersonalAdapter personalAdapter;

    public PersonalView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        personalAdapter = new PersonalAdapter(context, picasso);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

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
