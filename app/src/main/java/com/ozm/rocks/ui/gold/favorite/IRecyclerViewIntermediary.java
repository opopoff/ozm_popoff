package com.ozm.rocks.ui.gold.favorite;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Will on 2/8/2015.
 */
public interface IRecyclerViewIntermediary<T extends Object, VH extends RecyclerView.ViewHolder>{
    public int getCount();
    public T getItem(int position);
    public VH getViewHolder(ViewGroup viewGroup, int type);
    public int getItemViewType(int position);
    public void populateViewHolder(VH viewHolder, int position);
}