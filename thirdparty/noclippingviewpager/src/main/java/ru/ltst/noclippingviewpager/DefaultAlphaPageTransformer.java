package ru.ltst.noclippingviewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

public class DefaultAlphaPageTransformer implements ViewPager.PageTransformer
{
    private static float MIN_ALPHA = 0.5f;
    private static float MIN_SCALE = 0.0f;

    @Override
    public void transformPage(View page, float position)
    {
        if (position < -1)
        {
            page.setAlpha(0.5f);
        }
        else if (position <= 1)
        {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) /
                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        }
        else
        {
            page.setAlpha(0.5f);
        }
    }
}