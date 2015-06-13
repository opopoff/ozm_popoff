package com.ozm.rocks.ui.gold;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.misc.ListBindableAdapter;
import com.ozm.rocks.util.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GoldAdapter extends ListBindableAdapter<ImageResponse> {
    private Callback callback;
    private Picasso picasso;

    public GoldAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = picasso;
    }

    @Override
    protected int layoutId(int position) {
        return R.layout.gold_grid_item;
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
        view.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
        AspectRatioImageView mImageView = (AspectRatioImageView) view.findViewById(R.id.gold_grid_view_item);
        mImageView.setAspectRatio(item.width / (float) item.height);
        if (item.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        if (item.isGIF) {
            Ion.with(getContext()).load(item.url).withBitmap().intoImageView(mImageView).setCallback(
                    new FutureCallback<ImageView>() {
                        @Override
                        public void onCompleted(Exception e, ImageView result) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            picasso.load(item.url).
                    noFade().into(
                    mImageView, new com.squareup.picasso.Callback() {
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

    private void loadingImagesPreview() {
        for (int i = 0; i < getList().size(); i++) {
            ImageResponse image = this.getItem(i);
            if (!image.isGIF) {
                picasso.load(image.url).fetch();
            }
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
