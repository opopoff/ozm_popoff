package com.ozm.rocks.ui.misc;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerBindableAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    private List<T> dataset = new ArrayList<>();

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public T getItem(int position) {
        return dataset.get(position);
    }

    public void addAll(List<? extends T> items) {
        final int size = dataset.size();
        dataset.addAll(items);
        notifyItemRangeInserted(size, items.size());
    }

    public void deleteChild(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataset.size() - position - 1);
    }

    public void moveChildToTop(int position) {
        final T item = dataset.remove(position);
        dataset.add(0, item);
        notifyItemMoved(position, 0);
        notifyItemRangeChanged(0, dataset.size());
    }


}
