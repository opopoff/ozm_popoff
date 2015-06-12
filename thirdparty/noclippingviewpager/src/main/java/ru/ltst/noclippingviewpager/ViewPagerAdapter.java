package ru.ltst.noclippingviewpager;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;


/**
 * Created by dmitry on 21/01/15.
 */
public abstract class ViewPagerAdapter<T> extends PagerAdapter
{
    protected final @NonNull Context mContext;
    protected final @NonNull LayoutInflater mLayoutInflater;
    protected @NonNull List<T> mList = Collections.emptyList();
    protected final @NonNull ViewPager mViewPager;

    public ViewPagerAdapter(@NonNull Context context, @NonNull ViewPager viewPager)
    {
        mContext = context;
        mViewPager = viewPager;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void addAll(List<T> list)
    {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        View v = returnViewForInstance(container, position);
        ((ViewPager) container).addView(v, 0);
        return v;
    }

    protected abstract
    @LayoutRes
    int getItemLayoutId();

    protected abstract View returnViewForInstance(ViewGroup container, int position);

    protected View inflateView(ViewGroup container)
    {
        return mLayoutInflater.inflate(getItemLayoutId(), container, false);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }

    protected View getRootView(int position)
    {
        return mViewPager.getChildAt(position);
    }

    public T getItem(int position)
    {
        return mList.get(position);
    }

}
