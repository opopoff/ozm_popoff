package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.analytics.LocalyticsController;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainEmotionsView extends LinearLayout implements BaseView {

    @Inject
    MainActivity.Presenter mainPresenter;
    @Inject
    MainEmotionsPresenter emotionsPresenter;
    @Inject
    Picasso picasso;
    @Inject
    LocalyticsController localyticsController;

    @InjectView(R.id.categories_list_view)
    GridView mCategoriesList;

    private EmotionsListAdapter emotionsAdapter;

    public MainEmotionsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        emotionsAdapter = new EmotionsListAdapter(context, picasso, new EmotionsListAdapter.ActionListener() {
            @Override
            public void openGoldCategory(long categoryId, String categoryName) {
                localyticsController.openGoldenCollection(categoryName);
                emotionsPresenter.openGoldScreen(categoryId, categoryName);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        emotionsPresenter.takeView(this);

        mCategoriesList.setAdapter(emotionsAdapter);
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

    public EmotionsListAdapter getEmotionsAdapter() {
        return emotionsAdapter;
    }
}
