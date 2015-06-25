package com.ozm.rocks.ui.gold.favorite;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.gold.GoldItemView;
import com.ozm.rocks.ui.misc.RecyclerViewHeaderFooterAdapter;
import com.ozm.rocks.util.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldFavoriteAdapter extends RecyclerViewHeaderFooterAdapter<ImageResponse, GoldFavoriteAdapter.ViewHolder> {

    private final Context context;
    private final Callback callback;
    private final Picasso picasso;
    private LayoutInflater inflater;

    private List<ImageResponse> dataset = new ArrayList<>();

    public GoldFavoriteAdapter(Context context, Picasso picasso,
                               RecyclerView.LayoutManager manager,
                               Callback callback) {
        super(manager);

        this.context = context;
        this.picasso = picasso;
        this.callback = callback;
        inflater = LayoutInflater.from(context);
    }

    private void loadingImagesPreview() {
        for (int i = 0; i < dataset.size(); i++) {
            ImageResponse image = dataset.get(i);
            if (!image.isGIF) {
                picasso.load(image.url).fetch();
            }
        }
    }

    @Override
    public ImageResponse getItem(int position) {
        return dataset.get(position);
    }

    public void addAll(List<? extends ImageResponse> items) {
        final int size = dataset.size();
        dataset.addAll(items);
        notifyItemRangeInserted(size, items.size());
        loadingImagesPreview();
    }

    public void deleteChild(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataset.size() - position - 1);
    }

    public void moveChildToTop(int position) {
        final ImageResponse item = dataset.remove(position);
        dataset.add(0, item);
        notifyItemMoved(position, 0);
        notifyItemRangeChanged(0, dataset.size());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    protected int getItemType(int position) {
        return 0;
    }

    @Override
    protected ViewHolder onCreteItemViewHolder(ViewGroup parent, int type) {
        GoldItemView view = (GoldItemView) inflater.inflate(R.layout.gold_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.bindView(dataset.get(position), position, context, picasso, callback);
    }

    public interface Callback {
        void click(int position);
        void doubleTap(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.gold_grid_item_image)
        protected AspectRatioImageView imageView;

        @InjectView(R.id.gold_grid_item_like)
        protected View likeView;

        @InjectView(R.id.gold_grid_item_progress)
        protected ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bindView(ImageResponse item,
                             final int position,
                             final Context context,
                             final Picasso picasso,
                             final Callback callback) {
            View view = itemView;
            view.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
            likeView.setVisibility(item.liked ? View.VISIBLE : View.GONE);
            imageView.setAspectRatio(item.width / (float) item.height);
            imageView.setOnTabClickListener(new AspectRatioImageView.OnTabClickListener() {
                @Override
                public void call() {
                    if (callback != null) {
                        callback.click(position);
                    }
                }
            });
            imageView.setOnDoubleTabClickListener(new AspectRatioImageView.OnDoubleTabClickListener() {
                @Override
                public void call() {
                    if (callback != null) {
                        callback.doubleTap(position);
                    }
                }
            });
            if (item.mainColor != null) {
                imageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
            }
            progressBar.setVisibility(View.VISIBLE);

            if (item.isGIF) {
                Ion.with(context).load(item.url).withBitmap().intoImageView(imageView).setCallback(
                        new FutureCallback<ImageView>() {
                            @Override
                            public void onCompleted(Exception e, ImageView result) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else {
                picasso.load(item.url).noFade().into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        }
                );
            }
        }
    }
}
