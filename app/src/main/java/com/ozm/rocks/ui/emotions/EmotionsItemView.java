package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ozm.rocks.data.api.response.ImageResponse;

import butterknife.ButterKnife;

public class EmotionsItemView extends LinearLayout {

    public EmotionsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindTo(final ImageResponse image) {

    }
}
