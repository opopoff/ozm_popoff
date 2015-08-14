package com.ozm.rocks.ui.screen.main.personal;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.FixRecyclerView;
import com.ozm.rocks.ui.misc.GridInsetDecoration;
import com.ozm.rocks.ui.screen.main.MainActivity;
import com.ozm.rocks.ui.screen.main.MainComponent;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PersonalView extends FrameLayout implements BaseView {

    @Inject
    MainActivity.Presenter presenter;
    @Inject
    PersonalPresenter myPresenter;
    @Inject
    Picasso picasso;

    @InjectView(R.id.my_collection_grid_view)
    protected FixRecyclerView staggeredGridView;
    @InjectView(R.id.my_collection_empty_view)
    protected View emptyView;

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
        staggeredGridView.addItemDecoration(new GridInsetDecoration(getContext(), R.dimen.staggered_grid_inset));
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

    public void bindData(List<ImageResponse> imageResponses) {
        if (imageResponses.size() > 0) {
            preloadImages(imageResponses);
            emptyView.setVisibility(GONE);
            personalAdapter.clear();
            personalAdapter.addAll(imageResponses);
            personalAdapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(VISIBLE);
        }
        if (imageResponses.size() > 9) {
            myPresenter.openOnBoardingDialog();
        }

    }
}
