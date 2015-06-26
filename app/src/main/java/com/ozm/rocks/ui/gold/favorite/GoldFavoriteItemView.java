package com.ozm.rocks.ui.gold.favorite;

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
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoldFavoriteItemView extends FrameLayout {
    @InjectView(R.id.gold_grid_item_image)
    protected AspectRatioImageView imageView;

    @InjectView(R.id.gold_grid_item_like)
    protected View likeView;

    @InjectView(R.id.gold_grid_item_progress)
    protected ProgressBar progressBar;

    private OnTouchClickListener mOnTouchClickListener;

    public GoldFavoriteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindView(ImageResponse item,
                         final int position,
                         final Context context,
                         final Picasso picasso,
                         final GoldFavoriteAdapter.Callback callback) {
        getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
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

    public void setOnTouchClickListener(OnTouchClickListener onTouchClickListener) {
        this.mOnTouchClickListener = onTouchClickListener;
    }

    public interface OnTouchClickListener {
        void singleClick();
        void doubleTap();
    }

}
