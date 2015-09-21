package com.umad.wat.ui.screen.main.general;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umad.R;
import com.umad.wat.data.api.request.Action;
import com.umad.wat.data.api.request.DislikeRequest;
import com.umad.wat.data.api.request.LikeRequest;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.image.OzomeImageLoader;
import com.umad.wat.ui.misc.Misc;
import com.umad.wat.util.AnimationTools;
import com.umad.wat.util.AspectRatioImageView;
import com.umad.wat.util.PInfo;
import com.umad.wat.util.Timestamp;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GeneralItemView extends FrameLayout {

    @InjectView(R.id.image_view)
    protected AspectRatioImageView mImageView;
    @InjectView(R.id.image_view_container)
    protected FrameLayout imageViewContainer;
    @InjectView(R.id.like_button)
    protected ImageButton mLikeButton;
    @InjectView(R.id.share_button)
    protected ImageButton mShareButton;
    @InjectView(R.id.emotion_label)
    protected TextView mEmotionLabel;
    @InjectView(R.id.fast_share_one_button)
    protected ImageView mShareOne;
    @InjectView(R.id.fast_share_two_button)
    protected ImageView mShareTwo;
    @InjectView(R.id.progress)
    protected ProgressBar mProgress;
    @InjectView(R.id.general_item_like)
    protected ImageView likeIcon;

    public GeneralItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindView(final ImageResponse image,
                         final int position,
                         final OzomeImageLoader ozomeImageLoader,
                         final List<PInfo> imageMessengers,
                         final List<PInfo> gifMessengers,
                         final GeneralAdapter.Callback callback) {
        updateLikeButton(image);
        mLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                like(image, callback, position);
            }
        });

        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.share(image, position);
            }
        });
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                AnimationTools.likeAnimation(
                        image.liked ? R.drawable.ic_like_empty
                                : R.drawable.ic_star_big, likeIcon, null);
                like(image, callback, position);
                image.liked = !image.liked;
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                actionListener.share(image, position);
                return true;
            }
        });

        imageViewContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        mShareButton.setImageDrawable(Misc.getColorFilterDrawable(getResources(),
                R.drawable.ic_share, R.color.general_item_share_color));

//        mEmotionLabel.setVisibility(isShowEmotion ? View.VISIBLE : View.GONE);
//        mEmotionLabel.setText(image.categoryDescription);
//        mEmotionLabel.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickByEmotion(image, actionListener);
//            }
//        });
        mImageView.setAspectRatio(image.width / (float) image.height);

        mProgress.setVisibility(VISIBLE);
        ozomeImageLoader.load(image.isGIF ? OzomeImageLoader.GIF : OzomeImageLoader.IMAGE,
                image.url, mImageView, new OzomeImageLoader.Listener() {
                    @Override
                    public void onSuccess() {
                        mProgress.setVisibility(GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        if (image.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + image.mainColor));
        }
        PInfo sharePackage = null;
        PInfo sharePackageTwo = null;
        final List<PInfo> messengers = image.isGIF ? gifMessengers : imageMessengers;
        if (messengers.size() > 0) {
            mShareOne.setVisibility(VISIBLE);
            sharePackage = messengers.get(0);
            mShareOne.setImageBitmap(sharePackage.getIcon());
            if (messengers.size() > 1) {
                mShareTwo.setVisibility(VISIBLE);
                sharePackageTwo = messengers.get(1);
                mShareTwo.setImageBitmap(sharePackageTwo.getIcon());
            } else {
                mShareTwo.setVisibility(GONE);
            }
        } else {
            mShareOne.setVisibility(GONE);
            mShareTwo.setVisibility(GONE);
        }
        //TODO два раза одно и тоже по сути делаеться
        final PInfo finalSharePackage = sharePackage;
        final GestureDetector shareOneGestureDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        callback.fastShare(finalSharePackage, image);
                        return true;
                    }
                });
        final PInfo finalSharePackageTwo = sharePackageTwo;
        final GestureDetector shareTwoGestureDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        callback.fastShare(finalSharePackageTwo, image);
                        return true;
                    }
                });

        ((ViewGroup) mShareOne.getParent()).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mShareOne.setAlpha(0.5f);
                } else if (event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mShareOne.setAlpha(1.0f);
                }
                shareOneGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        ((ViewGroup) mShareTwo.getParent()).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mShareTwo.setAlpha(0.5f);
                } else if (event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mShareTwo.setAlpha(1.0f);
                }
                shareTwoGestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    private void updateLikeButton(ImageResponse image) {
        mLikeButton.setImageResource(image.liked ? R.drawable.ic_like_color : R.drawable.ic_like_empty);
    }

    private void clickByEmotion(ImageResponse image,
                                GeneralAdapterOld.ActionListener actionListener) {
        actionListener.clickByCategory(image.categoryId, image.categoryDescription);
    }

    private void like(ImageResponse image, @NonNull GeneralAdapter.Callback callback, int position) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(Action.getLikeDislikeHideActionForMainFeed(image.id, Timestamp.getUTC()));
        if (image.liked) {
            callback.dislike(position, new DislikeRequest(actions), image);
        } else {
            callback.like(position, new LikeRequest(actions), image);
        }
//        visualResponse(image);
    }

    private void visualResponse(ImageResponse image) {
        image.liked = !image.liked;
        if (!image.liked) {
            Toast.makeText(getContext(),
                    getContext().getString(R.string.main_feed_dislike_format_string,
                            image.categoryDescription), Toast.LENGTH_SHORT).show();
        }

        updateLikeButton(image);
    }
}
