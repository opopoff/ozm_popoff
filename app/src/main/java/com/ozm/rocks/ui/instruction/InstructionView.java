package com.ozm.rocks.ui.instruction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.ui.misc.Misc;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InstructionView extends FrameLayout implements BaseView {
    @Inject
    InstructionActivity.Presenter presenter;

    @InjectView(R.id.instruction_view_pager)
    ViewPager viewPager;
    @InjectView(R.id.instruction_vp_indicator)
    CirclePageIndicator circlePageIndicator;

    private InstructionAdapter instructionAdapter;

    public InstructionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            InstructionComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        instructionAdapter = new InstructionAdapter(context);
    }

    @OnClick(R.id.instruction_next)
    void next() {
        presenter.openMainScreen();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        ArrayList<Drawable> drawables = new ArrayList<>();
        drawables.add(Misc.getDrawable(R.drawable.emotion_label_bg, getResources()));
        drawables.add(Misc.getDrawable(R.drawable.fresh_bg, getResources()));
        drawables.add(Misc.getDrawable(R.drawable.orange_bt_bg, getResources()));
        instructionAdapter.addAll(drawables);
        viewPager.setAdapter(instructionAdapter);
        circlePageIndicator.setFillColor(getResources().getColor(android.R.color.white));
        circlePageIndicator.setStrokeColor(getResources().getColor(R.color.primary));
        circlePageIndicator.setPageColor(getResources().getColor(R.color.primary));
        circlePageIndicator.setViewPager(viewPager);
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
