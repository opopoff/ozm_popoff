package com.ozm.rocks.ui.screen.categories;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ozm.R;
import com.ozm.rocks.data.api.request.Action;
import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.util.AspectRatioImageView;
import com.ozm.rocks.util.FadeImageLoading;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.Timestamp;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CategoryListItemView extends FrameLayout {

    @InjectView(R.id.image_view)
    protected AspectRatioImageView mImageView;
    @InjectView(R.id.image_view_container)
    protected FrameLayout imageViewContainer;
    @InjectView(R.id.like_button)
    protected ImageButton mLikeButton;
    @InjectView(R.id.hide_button)
    protected Button mHideButton;
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

    public CategoryListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void bindTo(final ImageResponse image, final int position, @NonNull final CategoryListAdapter.ActionListener
            actionListener, List<PInfo> gifMessengers, List<PInfo> messengers, Picasso picasso) {
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

        imageViewContainer.setOnTouchListener(new OnTouchListener() {
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

        mImageView.setAspectRatio(image.width / (float) image.height);

        if (image.mainColor != null) {
            mImageView.setBackgroundColor(Color.parseColor("#" + image.mainColor));
        }
        mProgress.setVisibility(VISIBLE);
        picasso.load(image.url).noFade().into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProgress.setVisibility(GONE);
                        FadeImageLoading.animate(mImageView);
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
        PInfo sharePackage = null;
        PInfo sharePackageTwo = null;
        if (image.isGIF) {

            if (gifMessengers.size() > 0) {
                mShareOne.setVisibility(VISIBLE);
                sharePackage = gifMessengers.get(0);
                mShareOne.setImageBitmap(sharePackage.getIcon());
                if (gifMessengers.size() > 1) {
                    mShareTwo.setVisibility(VISIBLE);
                    sharePackageTwo = gifMessengers.get(1);
                    mShareTwo.setImageBitmap(sharePackageTwo.getIcon());
                } else {
                    mShareTwo.setVisibility(GONE);
                }
            } else {
                mShareOne.setVisibility(GONE);
            }

        } else {

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
            }
        }

        final PInfo finalSharePackage = sharePackage;
        final GestureDetector shareOneGestureDetector = new GestureDetector(getContext(), new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                actionListener.fastShare(finalSharePackage, image);
                return true;
            }
        });
        final PInfo finalSharePackageTwo = sharePackageTwo;
        final GestureDetector shareTwoGestureDetector = new GestureDetector(getContext(), new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                actionListener.fastShare(finalSharePackageTwo, image);
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