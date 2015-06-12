package com.ozm.rocks.ui.personal;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.AspectRatioImageView;
import com.ozm.rocks.util.FadeImageLoading;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonalAdapter extends ListBindableAdapter<ImageResponse> {
    private Callback callback;
    private Picasso picasso;

    public PersonalAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = picasso;
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.personal_grid_item;
    }

    @Override
    public void bindView(ImageResponse item, final int position, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.click(position);
                }
            }
        });
        ImageView like = ((ImageView) view.findViewById(R.id.my_collection_grid_view_like));
        ImageView share = ((ImageView) view.findViewById(R.id.my_collection_grid_view_share));
        if (item.liked) {
            like.setVisibility(View.VISIBLE);
            like.setImageResource(R.drawable.ic_history_liked);
        } else {
            like.setVisibility(View.GONE);
        }
        if (item.shared) {
            like.setVisibility(View.VISIBLE);
            share.setImageResource(R.drawable.ic_history_shared);
        } else {
            share.setVisibility(View.GONE);
        }
        final AspectRatioImageView imageView =
                (AspectRatioImageView) view.findViewById(R.id.my_collection_grid_view_item);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        imageView.setAspectRatio(item.width / (float) item.height);

        if (item.mainColor != null) {
            imageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        progressBar.setVisibility(View.VISIBLE);
        if (item.isGIF) {
            Ion.with(getContext()).load(item.url).withBitmap().intoImageView(imageView).setCallback(
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
                            FadeImageLoading.animate(imageView);
                        }

                        @Override
                        public void onError() {

                        }
                    }
            );
        }
    }

    private void loadingImagesPreview() {
        for (int i = 0; i < getList().size(); i++) {
            ImageResponse image = this.getItem(i);
            picasso.load(image.url).fetch();
        }
    }

    @Override
    public void addAll(List<? extends ImageResponse> items) {
        super.addAll(items);
        loadingImagesPreview();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void click(int position);
    }
}
