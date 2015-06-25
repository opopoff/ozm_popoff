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
import com.ozm.rocks.util.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldFavoriteAdapter extends RecyclerViewHeaderFooterAdapter<GoldFavoriteAdapter.ViewHolder> {
    private final Context context;
    private final Callback callback;
    private final Picasso picasso;
    private LayoutInflater inflater;
    private final Intermediart intermediart;

    public GoldFavoriteAdapter(Context context, Picasso picasso,
                               RecyclerView.LayoutManager manager,
                               Intermediart intermediary,
                               Callback callback) {
        super(manager, intermediary);

        this.context = context;
        this.picasso = picasso;
        this.intermediart = intermediary;
        this.callback = callback;
        inflater = LayoutInflater.from(context);
    }

    private void loadingImagesPreview() {
        for (int i = 0; i < intermediart.getCount(); i++) {
            ImageResponse image = intermediart.getItem(i);
            if (!image.isGIF) {
                picasso.load(image.url).fetch();
            }
        }
    }

    public ImageResponse getItem(int position) {
        return intermediart.getItem(position);
    }

    public void addAll(List<? extends ImageResponse> items) {
        final int size = intermediart.getCount();
        intermediart.addAll(items);
        notifyItemRangeInserted(size, items.size());
        loadingImagesPreview();
    }

    public void deleteChild(int position) {
        intermediart.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, intermediart.getCount() - position - 1);
    }

    public void moveChildToTop(int position) {
        final ImageResponse item = intermediart.remove(position);
        intermediart.add(0, item);
        notifyItemMoved(position, 0);
        notifyItemRangeChanged(0, intermediart.getCount());
    }

//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
//        GoldItemView view = (GoldItemView) inflater.inflate(R.layout.gold_item_view, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder viewHolder, int i) {
//        viewHolder.bindView(dataset.get(i), i, context, picasso, callback);
//    }

    @Override
    public int getItemCount() {
        return intermediart.getCount();
    }

    public interface Callback {
        void click(int position);
        void doubleTap(int position);
    }

    public static class Intermediart<T extends ImageResponse> implements IRecyclerViewIntermediary<T, ViewHolder> {

        private final Context context;
        private final Picasso picasso;
        private final Callback callback;
        private final LayoutInflater inflater;
        private List<T> dataset = new ArrayList<>();

        public Intermediart(Context context, Picasso picasso, Callback callback) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.picasso =  picasso;
            this.callback = callback;
        }

        public void add(T item) {
            dataset.add(item);
        }

        public void add(int position, T item) {
            dataset.add(position, item);
        }

        public void addAll(List<? extends T> items) {
            dataset.addAll(items);
        }

        public T remove(int position) {
            return dataset.remove(position);
        }

        public void moveToTop(int position) {
            final T item = dataset.remove(position);
            dataset.add(0, item);
        }

        @Override
        public int getCount() {
            return dataset.size();
        }

        @Override
        public T getItem(int position) {
            return dataset.get(position);
        }

        @Override
        public ViewHolder getViewHolder(ViewGroup parent, int type) {
            GoldItemView view = (GoldItemView) inflater.inflate(R.layout.gold_item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public void populateViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.bindView(dataset.get(position), position, context, picasso, callback);
        }
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
