package com.umad.rly.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.ozm.R;

public class RadioButtonCenter extends RadioButton {
    private Drawable buttonDrawable;

    public RadioButtonCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompoundButton, 0, 0);
        buttonDrawable = a.getDrawable(1);
        setButtonDrawable(android.R.color.transparent);
        a.recycle();

        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Drawable[] compoundDrawables = getCompoundDrawables();
                final Drawable compoundDrawable = compoundDrawables[1];
                compoundDrawable.setColorFilter(getResources().getColor(
                        isChecked ? R.color.accent : R.color.accent_light), PorterDuff.Mode.SRC_ATOP);
                setCompoundDrawables(null, compoundDrawable, null, null);
            }
        });
    }

    @Override
    public void setButtonDrawable(int resId) {
        super.setButtonDrawable(android.R.color.transparent);
        buttonDrawable = ContextCompat.getDrawable(getContext(), resId);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (buttonDrawable != null) {
            buttonDrawable.setState(getDrawableState());
            final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
            final int height = buttonDrawable.getIntrinsicHeight();


            int y = 0;

            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    y = getHeight() - height;
                    break;
                case Gravity.CENTER_VERTICAL:
                    y = (getHeight() - height) / 2;
                    break;
            }

            int buttonWidth = buttonDrawable.getIntrinsicWidth();
            int buttonLeft = (getWidth() - buttonWidth) / 2;
            buttonDrawable.setBounds(buttonLeft, y, buttonLeft + buttonWidth, y + height);
            // It's not worked on fly devices;
//            final boolean checked = isChecked();
//            buttonDrawable.setColorFilter(getResources().getColor(
//                    checked ? R.color.accent : android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            buttonDrawable.draw(canvas);
        }
    }
}
