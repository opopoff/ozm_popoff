package com.ozm.rocks.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ozm.R;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.LikeDislike;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.util.UrlFormat;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GeneralListItemView extends FrameLayout {

    @InjectView(R.id.image_view)
    SimpleDraweeView mImageView;
    @InjectView(R.id.like_button)
    Button mLikeButton;

    public GeneralListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindTo(final ImageResponse image, final int position, @NonNull final GeneralListAdapter.ActionListener
            actionListener) {
        mLikeButton.setText(image.liked ? "dislike" : "like");
        mLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                like(image, actionListener, position);
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                like(image, actionListener, position);
                return true;
            }
        });
        mImageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        mImageView.setAspectRatio(image.width / (float) image.height);

        Uri uri = UrlFormat.getImageUri(image.url);
        if (image.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + image.mainColor));
        }
        if (image.isGIF) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setAutoPlayAnimations(true)
                    .build();
            mImageView.setController(controller);
        } else {
            mImageView.setImageURI(uri);
        }
    }

    private void like(ImageResponse image, @NonNull GeneralListAdapter.ActionListener actionListener, int position) {
        ArrayList<LikeDislike> likeDislikes = new ArrayList<>();
        likeDislikes.add(new LikeDislike(image.id, System.currentTimeMillis(), image.categoryId));
        if (image.liked) {
            actionListener.dislike(position, new DislikeRequest(likeDislikes));
        } else {
            actionListener.like(position, new LikeRequest(likeDislikes));
        }
    }
}
