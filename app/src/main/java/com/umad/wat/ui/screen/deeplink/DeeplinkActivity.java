package com.umad.wat.ui.screen.deeplink;

import android.os.Bundle;

import com.umad.R;
import com.umad.wat.OzomeComponent;
import com.umad.wat.base.HasComponent;
import com.umad.wat.base.mvp.BaseActivity;
import com.umad.wat.base.mvp.BasePresenter;
import com.umad.wat.base.mvp.BaseView;
import com.umad.wat.base.navigation.activity.ActivityScreenSwitcher;
import com.umad.wat.data.analytics.LocalyticsController;
import com.umad.wat.ui.widget.WidgetService;

import javax.inject.Inject;

public class DeeplinkActivity extends BaseActivity implements HasComponent<DeeplinkComponent>{

    @Inject
    Presenter presenter;

    @Inject
    LocalyticsController localyticsController;

    private DeeplinkComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localyticsController.openApp(LocalyticsController.URL);
    }

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
