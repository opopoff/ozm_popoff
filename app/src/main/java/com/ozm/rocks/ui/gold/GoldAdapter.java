package com.ozm.rocks.ui.gold;

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
import com.ozm.rocks.util.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldAdapter extends RecyclerView.Adapter<GoldAdapter.ViewHolder> {
    private final Context context;
    private final Callback callback;
    private final Picasso picasso;
    private LayoutInflater inflater;

    private List<ImageResponse> dataset = new ArrayList<>();

    public GoldAdapter(Context context, Picasso picasso, Callback callback) {
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

    public ImageResponse getItem(int position) {
        return dataset.get(position);
    }

    public void addAll(List<? extends ImageResponse> items) {
        final int size = dataset.size();
        dataset.addAll(items);
        /**
         * Hack! Use notifyItemInserted(position) instead notifyDataSetChanged() due to bug:
         * http://stackoverflow.com/questions/26860875/recyclerview-staggeredgridlayoutmanager-refresh-bug
         */
        for (int i = size; i < dataset.size(); i++) {
            notifyItemInserted(i);
        }
        loadingImagesPreview();
    }

    public void deleteChild(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataset.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        GoldItemView view = (GoldItemView) inflater.inflate(R.layout.gold_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.bindView(dataset.get(i), i, context, picasso, callback);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public interface Callback {
        void click(int position);
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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.click(position);
                    }
                }
            });
            view.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
            likeView.setVisibility(item.liked ? View.VISIBLE : View.GONE);
            imageView.setAspectRatio(item.width / (float) item.height);
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
