package com.umad.wat.ui.screen.gold.favorite;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.umad.R;
import com.umad.wat.data.api.response.Category;
import com.umad.wat.ui.screen.gold.SpecialProjectTimerTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldFavoriteHeaderView extends LinearLayout {

    @InjectView(R.id.gold_favorite_header_save)
    protected Button saveButton;

    @InjectView(R.id.gold_favorite_header_time)
    protected SpecialProjectTimerTextView timerView;

    public GoldFavoriteHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void seOnSaveButtonLickListener(View.OnClickListener listener) {
        saveButton.setOnClickListener(listener);
    }

    public void bindData(Category category) {
        timerView.setPromoEnd(category.promoEnd);
    }
}
