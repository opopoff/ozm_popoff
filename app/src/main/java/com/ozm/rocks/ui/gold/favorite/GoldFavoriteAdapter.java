package com.ozm.rocks.ui.gold.favorite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.RecyclerBindableAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GoldFavoriteAdapter extends RecyclerBindableAdapter<ImageResponse, GoldFavoriteAdapter.ViewHolder> {

    private final Context context;
    private final Callback callback;
    private final Picasso picasso;

    public GoldFavoriteAdapter(Context context, Picasso picasso,
                               RecyclerView.LayoutManager manager,
                               Callback callback) {
        super(context, manager);

        this.context = context;
        this.picasso = picasso;
        this.callback = callback;
    }

    private void loadingImagesPreview() {
        for (int i = 0; i < getItemCount(); i++) {
            ImageResponse image = getItem(i);
            if (!image.isGIF) {
                picasso.load(image.url).fetch();
            }
        }
    }

    public void addAll(List<? extends ImageResponse> items) {
        super.addAll(items);
        loadingImagesPreview();
    }

    @Override
    protected int getItemType(int position) {
        return 0;
    }

    @Override
    protected int viewLayoutId(int type) {
        return R.layout.gold_favorite_item_view;
    }

    @Override
    protected ViewHolder viewHolder(View view, int type) {
        return new ViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder viewHolder, int position, int type) {
        viewHolder.bindView(getItem(position), position, context, picasso, callback);
    }

    public interface Callback {
        void click(int position);
        void doubleTap(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(ImageResponse item, final int position, final Context context,
                             final Picasso picasso, final Callback callback) {
            ((GoldFavoriteItemView) itemView).bindView(item, position, context, picasso, callback);
        }
    }
}
