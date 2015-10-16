package com.umad.wat.ui.screen.main.general;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.umad.R;
import com.umad.wat.data.api.request.DislikeRequest;
import com.umad.wat.data.api.request.LikeRequest;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.image.OzomeImageLoader;
import com.umad.wat.ui.misc.RecyclerBindableAdapter;
import com.umad.wat.data.model.PInfo;
import com.umad.wat.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class GeneralAdapter extends RecyclerBindableAdapter<ImageResponse, GeneralAdapter.ViewHolder> {

    private final Callback callback;
    private final OzomeImageLoader ozomeImageLoader;

    private List<PInfo> imageMessengers = Collections.emptyList();
    private List<PInfo> gifMessengers = Collections.emptyList();

    public GeneralAdapter(Context context, OzomeImageLoader ozomeImageLoader,
                          RecyclerView.LayoutManager manager, Callback callback) {
        super(context, manager);
        this.ozomeImageLoader = ozomeImageLoader;
        this.callback = callback;
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.main_general_item;
    }

    @Override
    protected ViewHolder viewHolder(View view, int type) {
        return new ViewHolder(view);
    }

    @Override
    protected int getItemType(int position) {
        return 0;
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder viewHolder, int position, int type) {
        viewHolder.bindView(getItem(position), position, ozomeImageLoader, imageMessengers, gifMessengers, callback);
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

    private void loadingImagesPreview(List<? extends ImageResponse> items) {
        for (int i = 0; i < items.size(); i++) {
            fetchImage(items.get(i));
        }
    }

    public void setMessengers(ArrayList<PInfo> pInfoMessengers, boolean isGIF) {
        if (isGIF) {
            gifMessengers = pInfoMessengers;
        } else {
            imageMessengers = pInfoMessengers;
        }
    }

    public interface Callback {
        void click(ImageResponse image, int position);

        void doubleTap(ImageResponse image, int position);

        void share(ImageResponse image, int position);

        void like(int itemPosition, LikeRequest likeRequest, ImageResponse image);

        void dislike(int itemPosition, DislikeRequest dislikeRequest, ImageResponse image);

        void clickByCategory(long categoryId, String categoryName);

        void fastShare(PInfo pInfo, ImageResponse image);

        void onBoarding();

        void newMaximumShowedDecide(int decide);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(final ImageResponse item, int position,
                             final OzomeImageLoader imageLoader,
                             final List<PInfo> imageMessengers,
                             final List<PInfo> gifMessengers,
                             final Callback callback) {
            Timber.d("BindViewID: " + Integer.toHexString(System.identityHashCode(itemView)));
            final GeneralItemView itemView = (GeneralItemView) this.itemView;
            itemView.bindView(item, position, imageLoader, imageMessengers, gifMessengers, callback);
        }
    }
}
