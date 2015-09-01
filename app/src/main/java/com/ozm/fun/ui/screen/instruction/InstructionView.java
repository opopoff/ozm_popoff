package com.ozm.fun.ui.screen.instruction;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ozm.R;
import com.ozm.fun.base.ComponentFinder;
import com.ozm.fun.base.mvp.BaseView;
import com.ozm.fun.data.analytics.LocalyticsController;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.ltst.noclippingviewpager.NoClippingViewPagerContainer;

public class InstructionView extends FrameLayout implements BaseView, ViewPager.OnPageChangeListener {
    @Inject
    InstructionActivity.Presenter presenter;

    @Inject
    LocalyticsController localyticsController;

    @InjectView(R.id.instruction_vp_indicator)
    protected CirclePageIndicator circlePageIndicator;
    @InjectView(R.id.view_pager_container)
    protected NoClippingViewPagerContainer viewPagerContainer;

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
        final Resources resources = getResources();
        ArrayList<InstructionAdapter.InstructionAdapterItem> drawables = new ArrayList<>();
        drawables.add(new InstructionAdapter.InstructionAdapterItem(R.mipmap.onboarding_screen_1));
        drawables.add(new InstructionAdapter.InstructionAdapterItem(R.mipmap.onboarding_screen_2));
        drawables.add(new InstructionAdapter.InstructionAdapterItem(R.mipmap.onboarding_screen_3));
        instructionAdapter.addAll(drawables);
        viewPagerContainer.setAdapter(instructionAdapter);
        viewPagerContainer.setPageMargin(resources.getDimensionPixelSize(R.dimen.instruction_view_pager_margin_page));
        viewPagerContainer.setOffscreenPageLimit(instructionAdapter.getCount());
        circlePageIndicator.setOnPageChangeListener(this);
        circlePageIndicator.setFillColor(resources.getColor(android.R.color.white));
        circlePageIndicator.setStrokeColor(resources.getColor(R.color.primary));
        circlePageIndicator.setPageColor(resources.getColor(R.color.primary));
        circlePageIndicator.setViewPager(viewPagerContainer.getViewPager());
        // Send event about opening of first page at ViewPage;
        localyticsController.showOnBoardingPage(1);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // nothing;
    }

    @Override
    public void onPageSelected(int position) {
        localyticsController.showOnBoardingPage(position + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // nothing;
    }
}
