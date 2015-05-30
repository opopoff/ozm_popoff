package com.ozm.rocks.ui.general;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ozm.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FilterListItemView extends LinearLayout {

    @InjectView(R.id.category_filter_item_text_view)
    protected TextView title;

    public FilterListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bind(FilterListItemData item) {
        title.setText(item.title);
    }
}
