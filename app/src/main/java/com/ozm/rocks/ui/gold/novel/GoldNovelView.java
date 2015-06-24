package com.ozm.rocks.ui.gold.novel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.gold.GoldActivity;
import com.ozm.rocks.ui.gold.GoldComponent;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class GoldNovelView extends LinearLayout implements BaseView {

    @Inject
    Picasso picasso;

    @Inject
    LikeHideResult mLikeHideResult;

    @Inject
    GoldActivity.Presenter parentPresenter;

    @Inject
    GoldNovelPresenter presenter;

    public GoldNovelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            GoldComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
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
