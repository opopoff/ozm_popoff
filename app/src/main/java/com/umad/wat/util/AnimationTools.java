package com.umad.wat.util;

import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.umad.R;
import com.umad.wat.ui.misc.OnEndAnimationListener;

public class AnimationTools {

    public static final long DURATION_LIKE_ANIMATION = 200;
    public static final long DURATION_NEW_ANIMATION = 800;
    public static final long DURATION_VK_ANIMATION = 400;

    private static final AlphaAnimation showAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);

    private static final ScaleAnimation showScaleAnimation = new ScaleAnimation(
            0.2f, 1.4f, 0.2f, 1.4f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);

    private static final ScaleAnimation toNormalScaleAnimation = new ScaleAnimation(
            1.4f, 1.0f, 1.4f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);

    private static final AlphaAnimation hideAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);

    private static final ScaleAnimation hideScaleAnimation = new ScaleAnimation(
            1.0f, 0.2f, 1.0f, 0.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);

    private AnimationTools() {
        //nothing;
    }

    public static void likeAnimation(@DrawableRes int icon,
                                     final ImageView imageView,
                                     final OnFinishListener listener) {
        imageView.setImageResource(icon);
        imageView.setVisibility(View.VISIBLE);
        AnimationSet showAnimationSet = showAnimationSet();
        showAnimationSet.setAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet toNormalAnimationSet = toNormalAnimationSet();
                toNormalAnimationSet.setAnimationListener(new OnEndAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AnimationSet hideAnimationSet = hideAnimationSet();
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
                });
                imageView.startAnimation(toNormalAnimationSet);
            }
        });
        imageView.startAnimation(showAnimationSet);
    }

    public static void likeAnimationWithTranslate(@DrawableRes int icon,
                                                  final ImageView imageView, final ImageView postImageView,
                                                  final OnFinishListener listener) {
        imageView.setImageResource(icon);
        imageView.setVisibility(View.VISIBLE);
        AnimationSet showAnimationSet = showAnimationSet();
        showAnimationSet.setAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet toNormalAnimationSet = toNormalAnimationSet();
                toNormalAnimationSet.setAnimationListener(new OnEndAnimationListener() {
                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        AnimationSet hideAnimationSet = translateHideAnimationSet(imageView, postImageView);
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
                });
                imageView.startAnimation(toNormalAnimationSet);
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
        ScaleAnimation showScaleAnimation = new ScaleAnimation(0.2f, 1.1f, 0.2f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        showScaleAnimation.setDuration(DURATION_VK_ANIMATION);
        showScaleAnimation.setInterpolator(container.getContext(), android.R.interpolator.decelerate_quint);
        showScaleAnimation.setAnimationListener(onEndAnimationListener);
        container.startAnimation(showScaleAnimation);
    }

    private static AnimationSet showAnimationSet() {
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(showAlphaAnimation);
        set.addAnimation(showScaleAnimation);
        set.setDuration(DURATION_LIKE_ANIMATION);
        return set;
    }

    private static AnimationSet hideAnimationSet() {
        AnimationSet set = new AnimationSet(false);
        set.setDuration(DURATION_LIKE_ANIMATION);
        set.addAnimation(hideAlphaAnimation);
        set.addAnimation(hideScaleAnimation);
        set.setStartOffset(DURATION_LIKE_ANIMATION * 2);
        //фикс потому что по непонятной мне причине offset накапливается
        set.restrictDuration(DURATION_LIKE_ANIMATION * 3);
        return set;
    }


    private static AnimationSet toNormalAnimationSet() {
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(toNormalScaleAnimation);
        set.setDuration(DURATION_LIKE_ANIMATION / 2);
        return set;
    }

    private static AnimationSet translateHideAnimationSet(ImageView imageView, ImageView postImageView) {
        AnimationSet set = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, getScaleX(imageView, postImageView),
                1.0f, getScaleY(imageView, postImageView),
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                0, getX(imageView, postImageView),
                0, getY(imageView, postImageView));
        set.setDuration(DURATION_LIKE_ANIMATION);
        set.setStartOffset(DURATION_LIKE_ANIMATION * 2);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);
        //фикс потому что по непонятной мне причине offset накапливается
        set.restrictDuration(DURATION_LIKE_ANIMATION * 3);
        return set;
    }

    private static float getScaleX(ImageView imageView, ImageView postImageView) {
        return ((float) postImageView.getWidth()) / (imageView.getWidth() * 2);
    }

    private static float getScaleY(ImageView imageView, ImageView postImageView) {
        return ((float) postImageView.getHeight()) / (imageView.getHeight() * 2);
    }

    private static int getX(ImageView imageView, ImageView postImageView) {
        return (int) ((postImageView.getX()
                + ((View) postImageView.getParent()).getX()
                + ((float) postImageView.getWidth() / 2))
                - (imageView.getX() + ((float) imageView.getWidth() / 2)));
    }

    private static float getY(ImageView imageView, ImageView postImageView) {
        return (int) ((postImageView.getY()
                + ((View) postImageView.getParent()).getY()
                + ((float) postImageView.getHeight() / 2))
                - (imageView.getY() + ((float) imageView.getHeight() / 2)));
    }

    public interface OnFinishListener {
        void call();
    }
}
