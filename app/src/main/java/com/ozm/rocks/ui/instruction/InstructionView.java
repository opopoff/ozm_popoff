package com.ozm.rocks.ui.instruction;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import ru.ltst.noclippingviewpager.NoClippingViewPagerContainer;

public class InstructionView extends FrameLayout implements BaseView {
    @Inject
    InstructionActivity.Presenter presenter;

    //    @InjectView(R.count.instruction_view_pager)
//    ViewPager viewPager;
    @InjectView(R.id.instruction_vp_indicator)
    CirclePageIndicator circlePageIndicator;
    @InjectView(R.id.view_pager_container)
    NoClippingViewPagerContainer viewPagerContainer;

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
        viewPagerContainer.setAdapter(instructionAdapter);
        viewPagerContainer.setPageMargin(getResources()
                .getDimensionPixelSize(R.dimen.instruction_view_pager_margin_page));
        viewPagerContainer.setOffscreenPageLimit(instructionAdapter.getCount());
        circlePageIndicator.setFillColor(getResources().getColor(android.R.color.white));
        circlePageIndicator.setStrokeColor(getResources().getColor(R.color.primary));
        circlePageIndicator.setPageColor(getResources().getColor(R.color.primary));
        circlePageIndicator.setViewPager(viewPagerContainer.getViewPager());
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
