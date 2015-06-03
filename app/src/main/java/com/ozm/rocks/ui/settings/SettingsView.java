package com.ozm.rocks.ui.settings;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.ui.main.MainComponent;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SettingsView extends LinearLayout implements BaseView {

    @Inject
    TokenStorage tokenStorage;

    @Inject
    SettingsPresenter presenter;

    @InjectView(R.id.setting_show_widget_switcher)
    protected SwitchCompat showWidgetSwitcher;

    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        MainComponent component = ComponentFinder.findActivityComponent(context);
        component.inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        showWidgetSwitcher.setChecked(tokenStorage.isShowWidget());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError(Throwable throwable) {

    }

    @OnClick(R.id.setting_show_widget_view)
    public void onClickByShowWidgetItem() {
        final boolean checked = !showWidgetSwitcher.isChecked();
        tokenStorage.showWidget(checked);
        showWidgetSwitcher.setChecked(checked);

        if (checked) {
            presenter.startService();
        } else {
            presenter.stopService();
        }
    }
}
