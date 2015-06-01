package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;

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

    public void bindTo(final EmotionsListAdapter.EmotionsListItem item, int position, final EmotionsListAdapter.ActionListener
            actionListener) {
        for (final Category category : item.getCategories()) {
            SimpleEmotionItemView view = (SimpleEmotionItemView) LayoutInflater.from(getContext()).
                    inflate(R.layout.simple_emotion_item_view, null);
            view.bindTo(category, null);
            this.addView(view);
        }
    }
}
