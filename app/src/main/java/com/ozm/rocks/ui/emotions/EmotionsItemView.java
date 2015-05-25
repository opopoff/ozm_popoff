package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

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

    public void bindTo(final EmotionsListAdapter.EmotionsListItem item, int position, EmotionsListAdapter.ActionListener
            actionListener) {

    }
}
