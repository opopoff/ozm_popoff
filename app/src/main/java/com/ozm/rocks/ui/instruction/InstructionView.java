package com.ozm.rocks.ui.instruction;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstructionView extends FrameLayout implements BaseView {
    @Inject
    InstructionActivity.Presenter presenter;

    public InstructionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            InstructionComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
    }

    @OnClick(R.id.instruction_next)
    void next() {
        presenter.openMainScreen();
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
