package com.ozm.rocks.ui.gold.favorite;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ozm.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldFavoriteHeaderView extends LinearLayout {

    @InjectView(R.id.gold_favorite_header_save)
    protected Button saveButton;

    @InjectView(R.id.gold_favorite_header_time)
    protected TextView timeView;

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
}
