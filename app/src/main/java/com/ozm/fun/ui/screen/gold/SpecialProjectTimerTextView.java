package com.ozm.fun.ui.screen.gold;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.fun.util.Strings;
import com.ozm.fun.util.Timestamp;

import java.util.concurrent.TimeUnit;

public class SpecialProjectTimerTextView extends TextView {

    private static final String DATE_FORMAT = "HH:mm:ss";
    private static final int SECOND = 1000;
    private static final int DAY_SECONDS = 24 * 60 * 60;

    private long promoEnd;

    private Handler handler;

    public SpecialProjectTimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler();
    }

    public void setPromoEnd(long promoEnd) {
        this.promoEnd = promoEnd;
        updateCounter();
        startTimer();

    }

    private void startTimer() {
        handler.postDelayed(runnable, SECOND);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (updateCounter() > 0) {
                startTimer();
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTimer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable);
    }

    public long updateCounter() {
        final long utcNow = Timestamp.getUTC();
        long millis = promoEnd - utcNow;
        if (millis < 0) {
            millis = 0L;
        }
        final int days = (int) TimeUnit.SECONDS.toDays(millis);
        final String hhmmss = Timestamp.getInterval(DATE_FORMAT, millis - days * DAY_SECONDS);
        setText(days > 0
                ? getResources().getQuantityString(R.plurals.days, days, days) + Strings.GUP + hhmmss
                : hhmmss);
        return millis;
    }
}
