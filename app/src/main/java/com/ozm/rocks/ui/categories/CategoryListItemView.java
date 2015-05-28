package com.ozm.rocks.ui.categories;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.ozm.R;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.Timestamp;
import com.ozm.rocks.util.UrlFormat;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CategoryListItemView extends FrameLayout {

    @InjectView(R.id.image_view)
    ImageView mImageView;
    @InjectView(R.id.like_button)
    ImageButton mLikeButton;
    @InjectView(R.id.hide_button)
    Button mHideButton;
    @InjectView(R.id.share_button)
    ImageButton mShareButton;
    @InjectView(R.id.emotion_label)
    TextView mEmotionLabel;
    @InjectView(R.id.fast_share_one_button)
    ImageButton mShareOne;
    @InjectView(R.id.fast_share_two_button)
    ImageButton mShareTwo;


    public CategoryListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindTo(final ImageResponse image, final int position, @NonNull final CategoryListAdapter.ActionListener
            actionListener, List<PInfo> gifMessengers, List<PInfo> messengers) {
        updateLikeButton(image);
        mLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                like(image, actionListener, position);
            }
        });

        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.share(image, position);
            }
        });
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                like(image, actionListener, position);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                actionListener.share(image, position);
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

        mHideButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide(image, actionListener, position);
            }
        });

        mShareButton.setImageResource(R.drawable.ic_share);

        mEmotionLabel.setVisibility(GONE);

//        mImageView.setAspectRatio(image.width / (float) image.height);

        mImageView.getLayoutParams().height =
                (int) (mImageView.getLayoutParams().width / (image.width / (float) image.height));

        Uri uri = UrlFormat.getImageUri(image.url);
        if (image.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + image.mainColor));
        }

        Glide.with(getContext()).load(image.url).fitCenter().into(mImageView);
        if (image.isGIF) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setAutoPlayAnimations(true)
                    .build();
//            mImageView.setController(controller);

            if (gifMessengers.size() > 0) {
                mShareOne.setVisibility(VISIBLE);
                final PInfo pInfo = gifMessengers.get(0);
                mShareOne.setImageDrawable(pInfo.getIcon());
                mShareOne.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actionListener.fastShare(pInfo, image);
                    }
                });
                if (gifMessengers.size() > 1) {
                    mShareTwo.setVisibility(VISIBLE);
                    final PInfo pInfoTwo = gifMessengers.get(1);
                    mShareTwo.setImageDrawable(pInfoTwo.getIcon());
                    mShareTwo.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            actionListener.fastShare(pInfoTwo, image);
                        }
                    });
                } else {
                    mShareTwo.setVisibility(GONE);
                }
            } else {
                mShareOne.setVisibility(GONE);
            }
        } else {
//            mImageView.setImageURI(uri);

            if (messengers.size() > 0) {
                mShareOne.setVisibility(VISIBLE);
                final PInfo pInfo = messengers.get(0);
                mShareOne.setImageDrawable(pInfo.getIcon());
                mShareOne.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actionListener.fastShare(pInfo, image);
                    }
                });
                if (messengers.size() > 1) {
                    mShareTwo.setVisibility(VISIBLE);
                    final PInfo pInfoTwo = messengers.get(1);
                    mShareTwo.setImageDrawable(pInfoTwo.getIcon());
                    mShareTwo.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            actionListener.fastShare(pInfoTwo, image);
                        }
                    });
                } else {
                    mShareTwo.setVisibility(GONE);
                }
            } else {
                mShareOne.setVisibility(GONE);
            }
        }
    }

    private void updateLikeButton(ImageResponse image) {
        mLikeButton.setImageResource(image.liked ? R.drawable.ic_like_color : R.drawable.ic_like_empty);
    }

    private void hide(ImageResponse image, CategoryListAdapter.ActionListener actionListener, int position) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(Action.getLikeDislikeHideAction(image.id, Timestamp.getUTC(), image.categoryId));
        actionListener.hide(position, new HideRequest(actions), image);
    }

    private void like(ImageResponse image, @NonNull CategoryListAdapter.ActionListener actionListener, int position) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(Action.getLikeDislikeHideAction(image.id, Timestamp.getUTC(), image.categoryId));
        if (image.liked) {
            actionListener.dislike(position, new DislikeRequest(actions), image);
        } else {
            actionListener.like(position, new LikeRequest(actions), image);
        }
        visualResponse(image);
    }

    private void visualResponse(ImageResponse image) {
        image.liked = !image.liked;
        Toast.makeText(getContext(),
                getContext().getString(image.liked ? R.string.main_feed_like_format_string :
                                R.string.main_feed_dislike_format_string,
                        image.categoryDescription), Toast.LENGTH_SHORT).show();
        updateLikeButton(image);
    }
}
