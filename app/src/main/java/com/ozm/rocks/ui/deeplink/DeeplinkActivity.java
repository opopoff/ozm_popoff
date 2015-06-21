package com.ozm.rocks.ui.deeplink;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.ui.widget.WidgetService;

import javax.inject.Inject;

public class DeeplinkActivity extends BaseActivity implements HasComponent<DeeplinkComponent>{

    @Inject
    Presenter presenter;

    private DeeplinkComponent component;

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerDeeplinkComponent.builder()
                .ozomeComponent(ozomeComponent)
                .build();
        component.inject(this);
    }

    @Override
    protected int layoutId() {
        return R.layout.deeplink_layout;
    }

    @Override
    protected BasePresenter<? extends BaseView> presenter() {
        return presenter;
    }

    @Override
    protected int viewId() {
        return R.id.deeplink_view;
    }

    @Override
    public DeeplinkComponent getComponent() {
        return component;
    }

    @DeeplinkScope
    public static final class Presenter extends BasePresenter<DeepllinkView> {

        private final ActivityScreenSwitcher screenSwitcher;

        @Inject
        public Presenter(ActivityScreenSwitcher screenSwitcher) {
            this.screenSwitcher = screenSwitcher;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            WidgetService.wakeUpApplication(getView().getContext());
        }
    }
}
