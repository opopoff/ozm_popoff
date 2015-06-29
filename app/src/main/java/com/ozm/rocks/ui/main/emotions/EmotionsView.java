package com.ozm.rocks.ui.main.emotions;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;
import com.ozm.rocks.ui.misc.GridInsetDecoration;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmotionsView extends FrameLayout implements BaseView {

    @Inject
    MainActivity.Presenter mainPresenter;
    @Inject
    EmotionsPresenter emotionsPresenter;
    @Inject
    Picasso picasso;
    @Inject
    LocalyticsController localyticsController;

    @InjectView(R.id.categories_list_view)
    protected RecyclerView gridView;

    private EmotionsAdapter emotionsAdapter;

    private final GridLayoutManager layoutManager;

    public EmotionsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }

        layoutManager = new GridLayoutManager(context,
                getContext().getResources().getInteger(R.integer.column_count),
                StaggeredGridLayoutManager.VERTICAL, false);

        emotionsAdapter = new EmotionsAdapter(context, layoutManager, picasso, new EmotionsAdapter.ActionListener() {
            @Override
            public void openGoldCategory(Category category) {
                localyticsController.openGoldenCollection(category.description);
                emotionsPresenter.openGoldScreen(category);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        emotionsPresenter.takeView(this);
        gridView.setLayoutManager(layoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());
        gridView.addItemDecoration(new GridInsetDecoration(getContext(), R.dimen.emotions_grid_inset));
        gridView.setAdapter(emotionsAdapter);
    }


    @Override
    protected void onDetachedFromWindow() {
        emotionsPresenter.dropView(this);
        ButterKnife.reset(this);
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

    public EmotionsAdapter getEmotionsAdapter() {
        return emotionsAdapter;
    }
}
