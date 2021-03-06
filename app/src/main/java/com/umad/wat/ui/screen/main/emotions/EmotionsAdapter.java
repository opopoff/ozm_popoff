package com.umad.wat.ui.screen.main.emotions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.umad.R;
import com.umad.wat.data.api.response.Category;
import com.umad.wat.data.image.OzomeImageLoader;
import com.umad.wat.ui.misc.RecyclerBindableAdapter;

import java.util.List;

public class EmotionsAdapter extends RecyclerBindableAdapter<Category, EmotionsAdapter.ViewHolder> {
    private static final int BASE_TYPE = 0;
    private static final int RATING_TYPE = 1;

    private final OzomeImageLoader ozomeImageLoader;
    private ActionListener actionListener;
    private EmotionsRatingView.OnRatingClickListener onRatingClickListener;

    public EmotionsAdapter(Context context,
                           RecyclerView.LayoutManager manager,
                           OzomeImageLoader ozomeImageLoader,
                           @NonNull ActionListener actionListener) {
        super(context, manager);
        this.actionListener = actionListener;
        this.ozomeImageLoader = ozomeImageLoader;
    }

    @Override
    public void addAll(List<? extends Category> items) {
        super.addAll(items);
        loadingImagesPreview();
    }

    private void loadingImagesPreview() {
        for (int i = 0; i < getRealItemCount(); i++) {
            if (getItem(i) != null) {
                ozomeImageLoader.fetch(OzomeImageLoader.IMAGE, getItem(i).backgroundImage);
            }
        }
    }

    @Override
    public int getGridSpan(int position) {
        if (position > mHeaders.size() - 1 && getItemType(position) == 1) {
            return getSpan();
        }
        return super.getGridSpan(position);
    }

    @Override
    protected int getItemType(int position) {
        if (getItem(position - mHeaders.size()) == null) {
            return RATING_TYPE;
        }
        return BASE_TYPE;
    }


    @Override
    protected void onBindItemViewHolder(ViewHolder viewHolder, int position, int type) {
        if (type == BASE_TYPE) {
            viewHolder.bindView(getItem(position), ozomeImageLoader, position, actionListener);
        } else if (type == RATING_TYPE) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setFullSpan(true);
            viewHolder.itemView.setLayoutParams(layoutParams);
            viewHolder.bindRatingView(onRatingClickListener);
        }
    }

    @Override
    protected int layoutId(int type) {
        if (type == BASE_TYPE) {
            return R.layout.main_emotions_item;
        } else {
            return R.layout.main_emotions_rating;
        }
    }

    @Override
    protected ViewHolder viewHolder(View view, int type) {
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface ActionListener {
        void openGoldCategory(Category item);
    }


    public void setOnRatingClickListener(EmotionsRatingView.OnRatingClickListener onRatingClickListener) {
        this.onRatingClickListener = onRatingClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(Category item, OzomeImageLoader ozomeImageLoader, int position, ActionListener callback) {
            final EmotionsItemView itemView = (EmotionsItemView) this.itemView;
            itemView.bindView(item, position, ozomeImageLoader, callback);
        }

        public void bindRatingView(EmotionsRatingView.OnRatingClickListener onRatingClickListener) {
            final EmotionsRatingView itemView = (EmotionsRatingView) this.itemView;
            itemView.stateGeneralState();
            if (onRatingClickListener != null) {
                itemView.setOnRatingClickListener(onRatingClickListener);
            }
        }
    }
}
