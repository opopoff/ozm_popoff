package com.ozm.rocks.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.util.Dimensions;


public class MainMenuItemView extends LinearLayout {
    private final ImageView iconView;
    private final TextView titleView;
    private final int mainTextColor;
    private final int mainBackColor;
    private final int selectedTextColor;
    private final int selectedBackColor;
    private final float textSize;

    public MainMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        boolean reverseColors = false;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MainMenuItemView, 0, 0);
        try {
            reverseColors = a.getBoolean(R.styleable.MainMenuItemView_reverseColors, false);
        } finally {
            a.recycle();
        }

        setOrientation(HORIZONTAL);
        setClickable(true);
        if (reverseColors) {
            mainTextColor = getResources().getColor(R.color.menu_item_reverse_normal_text_color);
            mainBackColor = getResources().getColor(R.color.menu_item_reverse_normal_bg_color);
            selectedTextColor = getResources().getColor(R.color.menu_item_reverse_selected_text_color);
            selectedBackColor = getResources().getColor(R.color.menu_item_reverse_selected_bg_color);
        } else {
            mainTextColor = getResources().getColor(R.color.menu_item_normal_text_color);
            mainBackColor = getResources().getColor(R.color.menu_item_normal_bg_color);
            selectedTextColor = getResources().getColor(R.color.menu_item_selected_text_color);
            selectedBackColor = getResources().getColor(R.color.menu_item_selected_bg_color);
        }
        textSize = getResources().getDimensionPixelSize(R.dimen.text_size_menu);

        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 3;
        params.gravity = GravityCompat.END | Gravity.CENTER_VERTICAL;
        iconView = new ImageView(context, attrs);
        final int paddingLeft = (int) Dimensions.pxFromDp(getContext(), 16);
        iconView.setPadding(paddingLeft, 0, 0, 0);
        addView(iconView, params);

        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 7;
        params.gravity = GravityCompat.START | Gravity.CENTER_VERTICAL;
        titleView = new TextView(context, attrs);
        final int paddingBottom = (int) Dimensions.pxFromDp(getContext(), 4);
        titleView.setPadding(titleView.getPaddingLeft(), titleView.getPaddingTop(),
                titleView.getPaddingRight(), getPaddingBottom() + paddingBottom);
        addView(titleView, params);

        iconView.setColorFilter(mainTextColor, PorterDuff.Mode.SRC_ATOP);
        titleView.setTextColor(mainTextColor);
        titleView.setTextSize(textSize);
        setBackgroundColor(mainBackColor);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        if (pressed) {
            iconView.setColorFilter(selectedTextColor, PorterDuff.Mode.SRC_ATOP);
            titleView.setTextColor(selectedTextColor);
            setBackgroundColor(selectedBackColor);
        } else {
            iconView.setColorFilter(mainTextColor, PorterDuff.Mode.SRC_ATOP);
            titleView.setTextColor(mainTextColor);
            setBackgroundColor(mainBackColor);
        }
    }
}
