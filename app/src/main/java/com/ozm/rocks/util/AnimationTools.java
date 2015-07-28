package com.ozm.rocks.util;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.PathInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.ozm.R;
import com.ozm.rocks.ui.misc.OnEndAnimationListener;

public class AnimationTools {

    public static final long DURATION_LIKE_ANIMATION = 200;
    public static final long DURATION_NEW_ANIMATION = 800;
    public static final long DURATION_VK_ANIMATION = 400;

    private AnimationTools() {
        //nothing;
    }

    public static void likeAnimation(@DrawableRes int icon,
                                     final ImageView imageView,
                                     final OnFinishListener listener) {
        imageView.setImageResource(icon);
        AlphaAnimation showAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        showAlphaAnimation.setDuration(DURATION_LIKE_ANIMATION);
        ScaleAnimation showScaleAnimation = new ScaleAnimation(0.2f, 1.4f, 0.2f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        showScaleAnimation.setDuration(DURATION_LIKE_ANIMATION);
        imageView.setVisibility(View.VISIBLE);
        AnimationSet showAnimationSet = new AnimationSet(false);
        showAnimationSet.addAnimation(showAlphaAnimation);
        showAnimationSet.addAnimation(showScaleAnimation);
        showAnimationSet.setAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation toNormalScaleAnimation = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                toNormalScaleAnimation.setDuration(DURATION_LIKE_ANIMATION / 2);
                toNormalScaleAnimation.setAnimationListener(new OnEndAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlphaAnimation hideAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                                hideAlphaAnimation.setDuration(DURATION_LIKE_ANIMATION);
                                ScaleAnimation hideScaleAnimation = new ScaleAnimation(1.0f, 0.2f, 1.0f, 0.2f,
                                        Animation.RELATIVE_TO_SELF, 0.5f,
                                        Animation.RELATIVE_TO_SELF, 0.5f);
                                hideScaleAnimation.setDuration(DURATION_LIKE_ANIMATION);
                                AnimationSet hideAnimationSet = new AnimationSet(false);
                                hideAnimationSet.addAnimation(hideAlphaAnimation);
                                hideAnimationSet.addAnimation(hideScaleAnimation);
                                hideAnimationSet.setAnimationListener(new OnEndAnimationListener() {
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        imageView.setVisibility(View.GONE);
                                        if (listener != null) {
                                            listener.call();
                                        }
                                    }
                                });
                                imageView.startAnimation(hideAnimationSet);
                            }
                        }, DURATION_LIKE_ANIMATION * 2);
                    }
                });
                imageView.startAnimation(toNormalScaleAnimation);
            }
        });
        imageView.startAnimation(showAnimationSet);
    }

    public static void likeAnimationWithTranslate(@DrawableRes int icon,
                                                  final ImageView imageView, final ImageView postImageView,
                                                  final OnFinishListener listener) {
        imageView.setImageResource(icon);
        AlphaAnimation showAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        showAlphaAnimation.setDuration(DURATION_LIKE_ANIMATION);
        ScaleAnimation showScaleAnimation = new ScaleAnimation(0.2f, 1.4f, 0.2f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        showScaleAnimation.setDuration(DURATION_LIKE_ANIMATION);
        imageView.setVisibility(View.VISIBLE);
        AnimationSet showAnimationSet = new AnimationSet(false);
        showAnimationSet.addAnimation(showAlphaAnimation);
        showAnimationSet.addAnimation(showScaleAnimation);
        showAnimationSet.setAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                final ScaleAnimation toNormalScaleAnimation = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                toNormalScaleAnimation.setDuration(DURATION_LIKE_ANIMATION / 2);
                toNormalScaleAnimation.setAnimationListener(new OnEndAnimationListener() {
                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        imageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                float scaleX = ((float) postImageView.getWidth()) / (imageView.getWidth() * 2);
                                float scaleY = ((float) postImageView.getHeight()) / (imageView.getHeight() * 2);
                                float x = (int) (((postImageView.getX() + ((float) postImageView.getWidth() / 2))
                                        - (imageView.getX() + ((float) imageView.getWidth() / 2))) );
                                float y = (int) (((postImageView.getY() + ((float) postImageView.getHeight() / 2))
                                        - (imageView.getY() + ((float) imageView.getHeight() / 2))) );
                                ScaleAnimation hideScaleAnimation = new ScaleAnimation(1.0f, scaleX, 1.0f, scaleY,
                                        Animation.RELATIVE_TO_SELF, 0.5f,
                                        Animation.RELATIVE_TO_SELF, 0.5f);
                                hideScaleAnimation.setDuration(DURATION_LIKE_ANIMATION );

//                                Path path = new Path();
//                                path.addArc(new RectF(0f - imageView.getHeight() / 2, 0f - imageView.getHeight() / 2,
//                                        x, y), -60f, 60f);
//                                PathAnimation translateAnimation = new PathAnimation(path);
                                TranslateAnimation translateAnimation = new TranslateAnimation(
                                        0, x,
                                        0, y);
                                translateAnimation.setDuration(DURATION_LIKE_ANIMATION);
                                AnimationSet hideAnimationSet = new AnimationSet(false);
                                hideAnimationSet.addAnimation(hideScaleAnimation);
                                hideAnimationSet.addAnimation(translateAnimation);
                                hideAnimationSet.setAnimationListener(new OnEndAnimationListener() {
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        imageView.setVisibility(View.GONE);
                                        if (listener != null) {
                                            listener.call();
                                        }
                                    }
                                });
                                imageView.startAnimation(hideAnimationSet);
                            }
                        }, DURATION_LIKE_ANIMATION * 2);
                    }
                });
                imageView.startAnimation(toNormalScaleAnimation);
            }
        });
        imageView.startAnimation(showAnimationSet);
    }

    public static void newImageAnimation(final View imageView) {
        ScaleAnimation showScaleAnimation = new ScaleAnimation(0.2f, 1f, 0.2f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        showScaleAnimation.setDuration(DURATION_NEW_ANIMATION);
        showScaleAnimation.setInterpolator(imageView.getContext(), android.R.interpolator.bounce);
        imageView.setVisibility(View.VISIBLE);
        imageView.startAnimation(showScaleAnimation);
    }

    public static void vkItemAnimation(final ImageView container, OnEndAnimationListener onEndAnimationListener) {
        container.setBackgroundResource(R.drawable.vk_anim_circle);
        ScaleAnimation showScaleAnimation = new ScaleAnimation(0.2f, 1f, 0.2f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        showScaleAnimation.setDuration(DURATION_VK_ANIMATION);
        showScaleAnimation.setInterpolator(container.getContext(), android.R.interpolator.overshoot);
        showScaleAnimation.setAnimationListener(onEndAnimationListener);
        container.startAnimation(showScaleAnimation);
    }

    public interface OnFinishListener {
        void call();
    }
}
