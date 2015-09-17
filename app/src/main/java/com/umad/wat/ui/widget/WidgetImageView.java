package com.umad.wat.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.umad.wat.util.BitmapTools;

public class WidgetImageView extends ImageView {
    public WidgetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                Bitmap imageBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
                Bitmap bitmap = BitmapTools.resizeBitmap(imageBitmap, getWidth(), getHeight());
                setImageBitmap(bitmap);
                return true;
            }
        });
    }
}
