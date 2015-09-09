package com.umad.rly.ui.screen.main.general;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ozm.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FilterView extends LinearLayout{

    @InjectView(R.id.main_general_filter_image_logo)
    protected ImageView logo;
    @InjectView(R.id.main_general_filter_text_view)
    protected TextView title;

    private boolean checked;

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        logo.setRotation(checked ? 180.f : 0.f);
    }

    public void setTitle(String text) {
        title.setText(text);
    }
}
