package com.ozm.rocks.util;

import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.ozm.rocks.ui.misc.OnEndAnimationListener;

public class AnimationTools {

    public static final long DURATION_LIKE_ANIMATION = 200;

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
                final ScaleAnimation toNormalScaleAnimation = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                toNormalScaleAnimation.setDuration(DURATION_LIKE_ANIMATION / 2);
                toNormalScaleAnimation.setAnimationListener(new OnEndAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                AlphaAnimation hideAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
//                                hideAlphaAnimation.setDuration(DURATION_LIKE_ANIMATION);
//                                ScaleAnimation hideScaleAnimation = new ScaleAnimation(1.0f, 0.2f, 1.0f, 0.2f,
//                                        Animation.RELATIVE_TO_SELF, 0.5f,
//                                        Animation.RELATIVE_TO_SELF, 0.5f);
//                                hideScaleAnimation.setDuration(DURATION_LIKE_ANIMATION);
//                                AnimationSet hideAnimationSet = new AnimationSet(false);
//                                hideAnimationSet.addAnimation(hideAlphaAnimation);
//                                hideAnimationSet.addAnimation(hideScaleAnimation);
//                                hideAnimationSet.setAnimationListener(new OnEndAnimationListener() {
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        imageView.setVisibility(View.GONE);
//                                        if (listener != null) {
//                                            listener.call();
//                                        }
//                                    }
//                                });
                                Animation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.0f,
                                        Animation.RELATIVE_TO_PARENT, 0.0f,
                                        Animation.RELATIVE_TO_PARENT, 1.0f,
                                        Animation.RELATIVE_TO_PARENT, 0.0f);
//                                TranslateAnimation translateAnimation = new TranslateAnimation(
//                                        Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
//                                        Animation.ABSOLUTE, 100, Animation.ABSOLUTE, 100);
//                                translateAnimation.setDuration(DURATION_LIKE_ANIMATION * 5);
//                                translateAnimation.setInterpolator(new DecelerateInterpolator());
                                translateAnimation.setAnimationListener(new OnEndAnimationListener() {
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        imageView.setVisibility(View.GONE);
                                        if (listener != null) {
                                            listener.call();
                                        }
                                    }
                                });
                                imageView.startAnimation(translateAnimation);
                            }
                        }, DURATION_LIKE_ANIMATION * 2);
                    }
                });
                imageView.startAnimation(toNormalScaleAnimation);
            }
        });
        imageView.startAnimation(showAnimationSet);
    }

    public static interface OnFinishListener {
        void call();
    }
}
