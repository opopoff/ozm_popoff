package com.ozm.rocks.ui.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SimpleEmotionItemView extends LinearLayout {

    @InjectView(R.id.simple_emotion_name)
    TextView mCategoryName;

    public SimpleEmotionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindTo(final Category category) {

        mCategoryName.setText(String.valueOf(category.description));
    }

}
