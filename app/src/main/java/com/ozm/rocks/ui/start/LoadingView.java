package com.ozm.rocks.ui.start;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoadingView extends FrameLayout implements BaseView {
    @Inject
    LoadingActivity.Presenter presenter;



    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            LoadingComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

    }

    @Override
    public void showLoading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showContent() {
        // Nothing;
    }

    @Override
    public void showError(Throwable throwable) {
        // TODO: implement no network error
    }
}
