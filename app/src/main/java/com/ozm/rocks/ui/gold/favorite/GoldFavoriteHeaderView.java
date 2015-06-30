package com.ozm.rocks.ui.gold.favorite;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.data.api.response.Category;
import com.ozm.rocks.util.Strings;
import com.ozm.rocks.util.Timestamp;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldFavoriteHeaderView extends LinearLayout {

    public static final String DATE_FORMAT = "HH:mm:ss";

    private static final int SECOND = 1000;
    private static final int DAY_SECONDS = 24 * 60 * 60;

    @InjectView(R.id.gold_favorite_header_save)
    protected Button saveButton;

    @InjectView(R.id.gold_favorite_header_time)
    protected TextView timeView;

    private long promoEnd;

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
        this.promoEnd = category.promoEnd;
        updateCounter();
        startTimer();
    }

    private void startTimer() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (updateCounter() > 0) {
                    startTimer();
                }
            }
        }, SECOND);
    }

    private long updateCounter() {
        final long utcNow = Timestamp.getUTC();
        long millis = promoEnd - utcNow;
        if (millis < 0) {
            millis = 0L;
        }
        final int days = (int) TimeUnit.SECONDS.toDays(millis);
        final String hhmmss = Timestamp.getInterval(DATE_FORMAT, millis - days * DAY_SECONDS);
        timeView.setText(days > 0
                ? getResources().getQuantityString(R.plurals.days, days, days) + Strings.GUP + hhmmss : hhmmss);
        return millis;
    }
}
