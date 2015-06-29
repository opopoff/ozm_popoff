package com.ozm.rocks.ui.main.personal;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.util.AspectRatioImageView;
import com.ozm.rocks.util.FadeImageLoading;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PesonalItemView extends FrameLayout {

    @InjectView(R.id.my_collection_grid_view_like)
    protected ImageView like;

    @InjectView(R.id.my_collection_grid_view_share)
    protected ImageView share;

    @InjectView(R.id.my_collection_grid_view_item)
    protected AspectRatioImageView imageView;

    @InjectView(R.id.progress)
    protected ProgressBar progressBar;

    public PesonalItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindView(final ImageResponse item,
                         final int position,
                         final Picasso picasso,
                         final PersonalAdapter.Callback callback) {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.click(position);
                }
            }
        });
        if (item.liked) {
            like.setVisibility(View.VISIBLE);
            like.setImageResource(R.drawable.ic_star_small);
        } else {
            like.setVisibility(View.GONE);
        }
        if (item.shared) {
            like.setVisibility(View.VISIBLE);
            share.setImageResource(R.drawable.ic_history_shared);
        } else {
            share.setVisibility(View.GONE);
        }
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
}
