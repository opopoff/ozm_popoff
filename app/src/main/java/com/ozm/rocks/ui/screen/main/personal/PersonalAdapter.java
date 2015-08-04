package com.ozm.rocks.ui.screen.main.personal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.image.OzomeImageLoader;
import com.ozm.rocks.ui.misc.RecyclerBindableAdapter;
import com.ozm.rocks.util.Strings;

import java.util.List;

public class PersonalAdapter extends RecyclerBindableAdapter<ImageResponse, PersonalAdapter.ViewHolder> {
    private Callback callback;
    private OzomeImageLoader ozomeImageLoader;

    public PersonalAdapter(Context context, RecyclerView.LayoutManager manager, OzomeImageLoader ozomeImageLoader) {
        super(context, manager);
        this.ozomeImageLoader = ozomeImageLoader;
    }

    private void loadingImagesPreview() {
        for (int i = 0; i < getRealItemCount(); i++) {
            fetchImage(getItem(i));
        }
    }

    private void fetchImage(ImageResponse item) {
        ozomeImageLoader.fetch(item.isGIF ? OzomeImageLoader.GIF : OzomeImageLoader.IMAGE,
                Strings.isBlank(item.thumbnailUrl) ? item.url : item.thumbnailUrl);
    }

    @Override
    public void addAll(List<? extends ImageResponse> items) {
        super.addAll(items);
        loadingImagesPreview();
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.main_personal_item_view;
    }

    @Override
    protected ViewHolder viewHolder(View view, int type) {
        return new ViewHolder(view);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected int getItemType(int position) {
        return 0;
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder viewHolder, int position, int type) {
        viewHolder.bindView(getItem(position), ozomeImageLoader, position, callback);
    }

    public interface Callback {
        void click(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(ImageResponse item, OzomeImageLoader ozomeImageLoader, int position, Callback callback) {
            final PersonalItemView itemView = (PersonalItemView) this.itemView;
            itemView.bindView(item, position, ozomeImageLoader, callback);
        }
    }
}
