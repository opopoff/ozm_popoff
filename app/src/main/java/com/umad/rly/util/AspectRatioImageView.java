package com.umad.rly.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import pl.droidsonroids.gif.GifImageView;

public class AspectRatioImageView extends GifImageView {

    private float aspectRatio;

    private OnDoubleTabClickListener onDoubleTabClickListener;
    private OnTabClickListener onTabClickListener;

    public AspectRatioImageView(Context context) {
        super(context);
        init();
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        final GestureDetector gestureDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (onDoubleTabClickListener != null) {
                            onDoubleTabClickListener.call();
                        }
                        return false;
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (onTabClickListener != null) {
                            onTabClickListener.call();
                        }
                        return false;
                    }
                });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onDoubleTabClickListener == null && onTabClickListener == null) {
                    return false;
                }
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    /**
     * Set the aspect ratio for this image view. This will update the view instantly.
     */
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (aspectRatio != 0) {
            int newWidth;
            int newHeight;
            newWidth = getMeasuredWidth();
            newHeight = Math.round(newWidth / aspectRatio);

            setMeasuredDimension(newWidth, newHeight);
        }
    }

    public void setOnDoubleTabClickListener(OnDoubleTabClickListener onDoubleTabClickListener) {
        this.onDoubleTabClickListener = onDoubleTabClickListener;
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public static interface OnDoubleTabClickListener {
        void call();
    }
    public static interface OnTabClickListener {
        void call();
    }
}