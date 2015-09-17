package com.umad.wat.ui.screen.main.emotions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.umad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmotionsRatingView extends FrameLayout {

    @InjectView(R.id.main_emotions_rating_text)
    protected TextView headerText;
    @InjectView(R.id.main_emotions_rating_green_btn)
    protected TextView greenBtn;
    @InjectView(R.id.main_emotions_rating_orange_btn)
    protected TextView orangeBtn;

    private OnRatingClickListener onRatingClickListener;

    public EmotionsRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmotionsRatingView(Context context, OnRatingClickListener onRatingClickListener) {
        super(context);
        this.onRatingClickListener = onRatingClickListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void stateGeneralState() {
        headerText.setText(getResources().getString(R.string.rating_general_state_header));
        greenBtn.setText(getResources().getString(R.string.rating_general_state_green));
        greenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRatingClickListener != null) {
                    onRatingClickListener.onFirstNo();
                }
                setBadState();
            }
        });
        orangeBtn.setText(getResources().getString(R.string.rating_general_state_orange));
        orangeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRatingClickListener != null) {
                    onRatingClickListener.onFirstYes();
                }
                setGoodState();
            }
        });
    }

    private void setGoodState() {
        headerText.setText(getResources().getString(R.string.rating_good_state_header));
        greenBtn.setText(getResources().getString(R.string.rating_good_state_green));
        greenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRatingClickListener != null) {
                    onRatingClickListener.onDismiss();
                }
            }
        });
        orangeBtn.setText(getResources().getString(R.string.rating_good_state_orange));
        orangeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRatingClickListener != null) {
                    onRatingClickListener.onGoodSuccess();
                }
            }
        });
    }

    private void setBadState() {
        headerText.setText(getResources().getString(R.string.rating_bad_state_header));
        greenBtn.setText(getResources().getString(R.string.rating_bad_state_green));
        greenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRatingClickListener != null) {
                    onRatingClickListener.onDismiss();
                }
            }
        });
        orangeBtn.setText(getResources().getString(R.string.rating_bad_state_orange));
        orangeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRatingClickListener != null) {
                    onRatingClickListener.onBadSuccess();
                }
            }
        });
    }


    public OnRatingClickListener getOnRatingClickListener() {
        return onRatingClickListener;
    }

    public void setOnRatingClickListener(OnRatingClickListener onRatingClickListener) {
        this.onRatingClickListener = onRatingClickListener;
    }

    public interface OnRatingClickListener {
        void onFirstYes();

        void onFirstNo();

        void onGoodSuccess();

        void onBadSuccess();

        void onDismiss();
    }
}
