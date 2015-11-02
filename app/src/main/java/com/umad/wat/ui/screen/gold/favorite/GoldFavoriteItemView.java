package com.umad.wat.ui.screen.gold.favorite;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.umad.R;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.image.OzomeImageLoader;
import com.umad.wat.util.AnimationTools;
import com.umad.wat.util.AspectRatioImageView;
import com.umad.wat.util.Strings;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class GoldFavoriteItemView extends FrameLayout {
    @InjectView(R.id.gold_grid_item_image)
    protected AspectRatioImageView imageView;

    @InjectView(R.id.gold_grid_item_like)
    protected ImageView likeView;

    @InjectView(R.id.gold_grid_item_share)
    protected ImageView share;

    @InjectView(R.id.gold_grid_item_new)
    protected ImageView newIcon;

    @InjectView(R.id.gold_grid_item_progress)
    protected ProgressBar progressBar;

    @InjectView(R.id.gold_grid_item_view_animation_like)
    protected ImageView animationLikeView;

    private OnTouchClickListener mOnTouchClickListener;

    public GoldFavoriteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindView(final ImageResponse item,
                         final int position,
                         final OzomeImageLoader ozomeImageLoader,
                         final GoldFavoriteAdapter.Callback callback) {
        String url;
        int width;
        int height;
        if (Strings.isBlank(item.thumbnailUrl) || item.thumbnailWidth == 0 || item.thumbnailHeight == 0) {
            url = item.url;
            width = item.width;
            height = item.height;
        } else {
            url = item.thumbnailUrl;
            width = item.thumbnailWidth;
            height = item.thumbnailHeight;
        }

        getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
        likeView.setVisibility(item.liked ? View.VISIBLE : View.GONE);
        if (item.isNewBlink) {
            item.isNewBlink = false;
            AnimationTools.newImageAnimation(this);
        }

        if (item.shared) {
            share.setVisibility(View.VISIBLE);
            share.setImageResource(R.drawable.ic_history_shared);
        } else {
            share.setVisibility(View.GONE);
            share.setImageResource(0);
        }
        if (item.isNew) {
            newIcon.setVisibility(View.VISIBLE);
            newIcon.setImageResource(R.drawable.ic_new);
        } else {
            newIcon.setVisibility(View.GONE);
            newIcon.setImageResource(0);
        }
        imageView.setImageDrawable(null);
        imageView.setAspectRatio(width / (float) height);
        imageView.setOnTabClickListener(new AspectRatioImageView.OnTabClickListener() {
            @Override
            public void call() {
                if (callback != null) {
                    callback.click(item, position);
                }
            }
        });
        imageView.setOnDoubleTabClickListener(
                new AspectRatioImageView.OnDoubleTabClickListener() {
                    @Override
                    public void call() {
                        if (callback != null) {
                            callback.doubleTap(item, position);
                        }
                        likeAnimation();
                    }
                });
        if (item.mainColor != null) {
            imageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        progressBar.setVisibility(View.VISIBLE);
        ozomeImageLoader.load(item.isGIF ? OzomeImageLoader.GIF : OzomeImageLoader.IMAGE, url, imageView,
                new OzomeImageLoader.Listener() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        // noting;
                    }
                });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void likeAnimation() {
        likeView.setVisibility(VISIBLE);
        likeView.setAlpha(0f);
        AnimationTools.likeAnimationWithTranslate(R.drawable.ic_star_big, animationLikeView, likeView,
                new AnimationTools.OnFinishListener() {
                    @Override
                    public void call() {
                        likeView.setAlpha(1f);
                    }
                });
    }

    public void setOnTouchClickListener(OnTouchClickListener onTouchClickListener) {
        this.mOnTouchClickListener = onTouchClickListener;
    }

    public interface OnTouchClickListener {
        void singleClick();

        void doubleTap();
    }

}
