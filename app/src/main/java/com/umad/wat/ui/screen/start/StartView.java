package com.umad.wat.ui.screen.start;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.umad.wat.base.ComponentFinder;
import com.umad.wat.base.mvp.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class StartView extends FrameLayout implements BaseView {

    @Inject
    StartActivity.Presenter presenter;

    public StartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            StartComponent component = ComponentFinder.findActivityComponent(context);
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
