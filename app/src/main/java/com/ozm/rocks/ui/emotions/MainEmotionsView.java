package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.ui.main.MainActivity;
import com.ozm.rocks.ui.main.MainComponent;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class MainEmotionsView extends LinearLayout implements BaseView {

    @Inject
    MainActivity.Presenter mainPresenter;
    @Inject
    MainEmotionsPresenter emotionsPresenter;

    public MainEmotionsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            MainComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        emotionsPresenter.takeView(this);
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
}
