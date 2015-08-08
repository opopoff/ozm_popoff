package com.ozm.rocks.ui.screen.main.personal;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ozm.R;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.image.OzomeImageLoader;
import com.ozm.rocks.util.AspectRatioImageView;
import com.ozm.rocks.util.Strings;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PersonalItemView extends FrameLayout {

    @InjectView(R.id.my_collection_grid_view_like)
    protected ImageView like;

    @InjectView(R.id.my_collection_grid_view_share)
    protected ImageView share;

    @InjectView(R.id.my_collection_grid_view_item)
    protected AspectRatioImageView imageView;

    @InjectView(R.id.progress)
    protected ProgressBar progressBar;

    public PersonalItemView(Context context, AttributeSet attrs) {
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
                         final PersonalAdapter.Callback callback) {

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
            like.setImageResource(0);
        }
        if (item.shared) {
            share.setVisibility(View.VISIBLE);
            share.setImageResource(R.drawable.ic_history_shared);
        } else {
            share.setVisibility(View.GONE);
            share.setImageResource(0);
        }
        imageView.setAspectRatio(width / (float) height);

        if (item.mainColor != null) {
            imageView.setBackgroundColor(Color.parseColor("#" + item.mainColor));
        }
        progressBar.setVisibility(View.VISIBLE);

        ozomeImageLoader.load(item.isGIF ? OzomeImageLoader.GIF : OzomeImageLoader.IMAGE, url, imageView,
                new OzomeImageLoader.Listener() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        // noting;
                    }
                });
    }
}
