package com.ozm.rocks.ui.main.emotions;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;
import com.ozm.rocks.ui.misc.GridInsetDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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

    private EmotionsHeaderView header;

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

    public void bindData(CategoryResponse category) {
        Category promo = null;
        List<Category> categories = new ArrayList<>(category.categories);
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).isPromo) {
                promo = categories.remove(i);
                break;
            }
        }
        if (promo != null && header == null) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            header = (EmotionsHeaderView) inflater.inflate(
                    R.layout.main_emotions_header_view, null, false);
            header.bindData(promo, picasso);
            final Category finalPromo = promo;
            header.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    localyticsController.openGoldenCollection(finalPromo.description);
                    emotionsPresenter.openGoldScreen(finalPromo);

                }
            });
            emotionsAdapter.addHeader(header);
        } else if (promo == null && header != null) {
            emotionsAdapter.removeHeader(header);
            header = null;
        }
        emotionsAdapter.clear();
        emotionsAdapter.addAll(categories);
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
