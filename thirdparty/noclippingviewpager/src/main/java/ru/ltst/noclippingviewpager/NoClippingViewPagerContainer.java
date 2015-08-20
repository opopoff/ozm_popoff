package ru.ltst.noclippingviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * Created by dmitry on 21/01/15.
 */
public class NoClippingViewPagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener {
    private PagerContainer mPagerContainer;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private final boolean vertical;

    public NoClippingViewPagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NoClippingViewPagerContainer, 0, 0);
        int pageWidth = a.getDimensionPixelSize(R.styleable.NoClippingViewPagerContainer_pageWidth,
                LayoutParams.MATCH_PARENT);
        int viewPagerHeight = a.getDimensionPixelSize(R.styleable.NoClippingViewPagerContainer_viewPagerHeight,
                LayoutParams.MATCH_PARENT);
        vertical = a.getBoolean(R.styleable.NoClippingViewPagerContainer_vertical, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.vp_layout, this, true);

        mPagerContainer = (PagerContainer) getChildAt(0);
        mViewPager = (ViewPager) mPagerContainer.getChildAt(0);

        mPagerContainer.getLayoutParams().height = viewPagerHeight;
        mPagerContainer.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        mViewPager.getLayoutParams().width = pageWidth;
        if (vertical) {
            mPagerContainer.setVertical(vertical);
            mViewPager.setRotation(90);
            mPagerContainer.getLayoutParams().height = LayoutParams.MATCH_PARENT;
            mPagerContainer.getLayoutParams().width = viewPagerHeight;
            mViewPager.getLayoutParams().height = pageWidth;
        }

        mViewPager.setOnPageChangeListener(this);

        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        setClipChildren(false);

        setPageTransformer(true, new DefaultAlphaPageTransformer());
    }

    public NoClippingViewPagerContainer(Context context) {
        this(context, null);
    }

    @Override
    public void setClipChildren(boolean clipChildren) {
        mViewPager.setClipChildren(clipChildren);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPagerContainer.invalidate();
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void setPageTransformer(boolean b, ViewPager.PageTransformer pageTransformer) {
        mViewPager.setPageTransformer(b, pageTransformer);
    }

    public void setPageMargin(int i) {
        mViewPager.setPageMargin(i);
    }

    public void setOffscreenPageLimit(int count) {
        mViewPager.setOffscreenPageLimit(count);
    }

    public void setAdapter(PagerAdapter adapter) {
        setOffscreenPageLimit(adapter.getCount());
        mViewPager.setAdapter(adapter);
    }

    public void setContentPageWidth(int width) {
        if (vertical) {
            mViewPager.getLayoutParams().height = width;
        } else {
            mPagerContainer.getLayoutParams().width = width;
        }
    }

    public void setContentPageHeight(int height) {
        if (vertical) {
            mPagerContainer.getLayoutParams().width = height;
        } else {
            mViewPager.getLayoutParams().height = height;
        }
    }

}
