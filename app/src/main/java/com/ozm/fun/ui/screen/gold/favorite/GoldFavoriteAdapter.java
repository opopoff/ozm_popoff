package com.ozm.fun.ui.screen.gold.favorite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ozm.R;
import com.ozm.fun.data.api.response.ImageResponse;
import com.ozm.fun.data.image.OzomeImageLoader;
import com.ozm.fun.ui.misc.RecyclerBindableAdapter;
import com.ozm.fun.util.Strings;

import java.util.List;

import timber.log.Timber;

public class GoldFavoriteAdapter extends RecyclerBindableAdapter<ImageResponse, GoldFavoriteAdapter.ViewHolder> {

    private final Callback callback;
    private final OzomeImageLoader ozomeImageLoader;

    private int maximumDecide;

    private OnDecideListener onDecideListener;

    public GoldFavoriteAdapter(Context context, OzomeImageLoader ozomeImageLoader,
                               RecyclerView.LayoutManager manager,
                               Callback callback) {
        super(context, manager);

        this.ozomeImageLoader = ozomeImageLoader;
        this.callback = callback;
    }

    private void loadingImagesPreview(List<? extends ImageResponse> items) {
        for (int i = 0; i < items.size(); i++) {
            fetchImage(items.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return getRealItemCount();
    }

    @Override
    public void add(int position, ImageResponse item) {
        super.add(position, item);
        fetchImage(item);
    }

    private void fetchImage(ImageResponse item) {
        ozomeImageLoader.fetch(item.isGIF ? OzomeImageLoader.GIF : OzomeImageLoader.IMAGE,
                Strings.isBlank(item.thumbnailUrl) ? item.url : item.thumbnailUrl);
    }

    public void addAll(List<? extends ImageResponse> items) {
        super.addAll(items);
        loadingImagesPreview(items);
    }

    @Override
    protected int getItemType(int position) {
        return 0;
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.gold_favorite_item_view;
    }

    @Override
    protected ViewHolder viewHolder(View view, int type) {
        return new ViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder viewHolder, int position, int type) {
        viewHolder.bindView(getItem(position), position, ozomeImageLoader, callback);
        int decide = position / 10;
        if (decide > maximumDecide) {
            maximumDecide = decide;
            if (onDecideListener != null) {
                onDecideListener.callDecide(maximumDecide * 10);
            }
        }
    }

    public void setOnDecideListener(OnDecideListener onDecideListener) {
        this.onDecideListener = onDecideListener;
    }

    public interface Callback {
        void click(ImageResponse image, int position);

        void doubleTap(ImageResponse image, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(ImageResponse item, int position,
                             final OzomeImageLoader imageLoader, final Callback callback) {
            Timber.d("BindViewID: " + Integer.toHexString(System.identityHashCode(itemView)));
            final GoldFavoriteItemView itemView = (GoldFavoriteItemView) this.itemView;
            itemView.bindView(item, position, imageLoader, callback);
        }
    }

    public static interface OnDecideListener {
        void callDecide(int count);
    }
}
