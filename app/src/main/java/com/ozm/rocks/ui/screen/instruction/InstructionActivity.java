package com.ozm.rocks.ui.screen.instruction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ozm.R;
import com.ozm.rocks.OzomeComponent;
import com.ozm.rocks.base.HasComponent;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.base.mvp.BasePresenter;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.base.navigation.activity.ActivityScreen;
import com.ozm.rocks.base.navigation.activity.ActivityScreenSwitcher;
import com.ozm.rocks.ui.screen.main.MainActivity;
import com.ozm.rocks.ui.screen.widget.WidgetController;

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
