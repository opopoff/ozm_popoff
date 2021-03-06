package com.umad.wat.ui.screen.main.emotions;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;
import com.umad.R;
import com.umad.wat.base.ComponentFinder;
import com.umad.wat.base.mvp.BaseView;
import com.umad.wat.data.analytics.LocalyticsController;
import com.umad.wat.data.api.response.Category;
import com.umad.wat.data.api.response.CategoryResponse;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.image.OzomeImageLoader;
import com.umad.wat.data.prefs.rating.RatingStorage;
import com.umad.wat.ui.ApplicationSwitcher;
import com.umad.wat.ui.misc.FixRecyclerView;
import com.umad.wat.ui.misc.GridInsetDecoration;
import com.umad.wat.ui.screen.main.MainActivity;
import com.umad.wat.ui.screen.main.MainComponent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmotionsView extends FrameLayout implements BaseView {
    private static final int RATING_VIEW_POSITION = 2;

    @Inject
    MainActivity.Presenter mainPresenter;
    @Inject
    EmotionsPresenter emotionsPresenter;
    @Inject
    Picasso picasso;
    @Inject
    OzomeImageLoader ozomeImageLoader;
    @Inject
    LocalyticsController localyticsController;
    @Inject
    ApplicationSwitcher applicationSwitcher;

    @InjectView(R.id.categories_list_view)
    protected FixRecyclerView gridView;

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

        emotionsAdapter = new EmotionsAdapter(context, layoutManager, ozomeImageLoader,
                new EmotionsAdapter.ActionListener() {
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
        gridView.setLayoutManager(layoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());
        gridView.addItemDecoration(new GridInsetDecoration(getContext(), R.dimen.emotions_grid_inset));
        gridView.setAdapter(emotionsAdapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        emotionsPresenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        emotionsPresenter.dropView(this);
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void bindData(CategoryResponse category, boolean showRatingView) {
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
                    R.layout.main_emotions_header, null, false);
            header.bindData(promo);
            emotionsPresenter.loadSpecialProject();
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

        if (showRatingView) {
            localyticsController.setMeduza(LocalyticsController.SHOW);
            categories.add(RATING_VIEW_POSITION, null);
            emotionsPresenter.setRatingStatus(RatingStorage.IGNORED);
        }
        emotionsAdapter.setOnRatingClickListener(new EmotionsRatingView.OnRatingClickListener() {
            @Override
            public void onFirstYes() {
                localyticsController.setMeduza(LocalyticsController.FIRST_YES);
            }

            @Override
            public void onFirstNo() {
                localyticsController.setMeduza(LocalyticsController.FIRST_NO);
            }

            @Override
            public void onGoodSuccess() {
                emotionsAdapter.deleteChild(RATING_VIEW_POSITION + emotionsAdapter.getHeadersCount());
                applicationSwitcher.openGooglePlayAppPage();
                emotionsPresenter.setRatingStatus(RatingStorage.SHOWED);
                localyticsController.setMeduza(LocalyticsController.SECOND_YES);
            }

            @Override
            public void onBadSuccess() {
                emotionsAdapter.deleteChild(RATING_VIEW_POSITION + emotionsAdapter.getHeadersCount());
                applicationSwitcher.openFeedbackEmailApplication();
                emotionsPresenter.setRatingStatus(RatingStorage.SHOWED);
                localyticsController.setMeduza(LocalyticsController.SECOND_YES);
            }

            @Override
            public void onDismiss() {
                emotionsAdapter.deleteChild(RATING_VIEW_POSITION + emotionsAdapter.getHeadersCount());
                emotionsPresenter.setRatingStatus(RatingStorage.NOT_RATED);
                localyticsController.setMeduza(LocalyticsController.SECOND_NO);
            }
        });
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

    //for compatibility
    public void bindSpecialProject(List<ImageResponse> specialProjectImages) {
        if (header != null) {
            header.bindData(specialProjectImages, picasso);
        }
    }

    public void bindSpecialProject(String url) {
        if (header != null) {
            header.bindData(url, ozomeImageLoader);
        }
    }

    public void clearAdapter() {
        emotionsAdapter.clear();
    }
}
