package com.ozm.rocks.ui.misc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Danil on 24.07.2015.
 */
public class FixRecyclerView extends RecyclerView {

    public FixRecyclerView(Context context) {
        super(context);
    }

    public FixRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException exception) {
            /**
             *  The mLayout has been disposed of before the
             *  RecyclerView and this stops the application
             *  from crashing.
             */
        }
    }
}
