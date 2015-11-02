package com.umad.wat.ui.screen.instruction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.umad.R;
import com.umad.wat.OzomeComponent;
import com.umad.wat.base.HasComponent;
import com.umad.wat.base.mvp.BaseActivity;
import com.umad.wat.base.mvp.BasePresenter;
import com.umad.wat.base.mvp.BaseView;
import com.umad.wat.base.navigation.activity.ActivityScreen;
import com.umad.wat.base.navigation.activity.ActivityScreenSwitcher;
import com.umad.wat.ui.screen.main.MainActivity;
import com.umad.wat.ui.widget.WidgetController;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public class InstructionActivity extends BaseActivity implements HasComponent<InstructionComponent> {

    @Inject
    Presenter presenter;

    @Inject
    WidgetController widgetController;

    private InstructionComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
        widgetController.checkOnRunning();
    }

    @Override
    protected void onCreateComponent(OzomeComponent ozomeComponent) {
        component = DaggerInstructionComponent.builder().
                ozomeComponent(ozomeComponent).build();
        component.inject(this);
    }

    @Override
    protected void onDestroy() {
        component = null;
        super.onDestroy();
    }

    @Override
    protected int layoutId() {
        return R.layout.instruction_layout;
    }

    @Override
    protected BasePresenter<? extends BaseView> presenter() {
        return presenter;
    }

    @Override
    protected int viewId() {
        return R.id.instruction;
    }

    @Override
    public InstructionComponent getComponent() {
        return component;
    }

    @InstructionScope
    public static final class Presenter extends BasePresenter<InstructionView> {
        private final ActivityScreenSwitcher screenSwitcher;
        private CompositeSubscription subscriptions;

        @Inject
        public Presenter(ActivityScreenSwitcher screenSwitcher) {
            this.screenSwitcher = screenSwitcher;
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            subscriptions = new CompositeSubscription();
        }

        @Override
        protected void onDestroy() {
            if (subscriptions != null) {
                subscriptions.unsubscribe();
                subscriptions = null;
            }
            super.onDestroy();
        }

        public void openMainScreen() {
            screenSwitcher.open(new MainActivity.Screen());
        }
    }

    public static final class Screen extends ActivityScreen {
        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        @Override
        protected Class<? extends Activity> activityClass() {
            return InstructionActivity.class;
        }
    }
}
