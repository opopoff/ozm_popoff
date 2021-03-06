package com.umad.wat.ui.screen.deeplink;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.umad.wat.base.ComponentFinder;
import com.umad.wat.base.mvp.BaseView;

public class DeepllinkView extends LinearLayout implements BaseView {
    public DeepllinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DeeplinkComponent component = ComponentFinder.findActivityComponent(context);
        component.inject(this);
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
}
