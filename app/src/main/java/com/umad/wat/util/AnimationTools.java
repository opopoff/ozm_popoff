package com.umad.wat.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.umad.R;
import com.umad.wat.ui.misc.OnEndAnimationListener;
import com.umad.wat.ui.misc.OnEndAnimatorListener;

public class AnimationTools {

    public static final long DURATION_LIKE_ANIMATION = 200;
    public static final long DURATION_NEW_ANIMATION = 800;
    public static final long DURATION_VK_ANIMATION = 400;

    private AnimationTools() {
        //nothing;
    }

    public static void likeAnimation(@DrawableRes int icon,
                                     final ImageView view,
                                     final OnFinishListener listener) {
        beforeAnimation(view, null, icon);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(
                showAnimatorSet(view),
                toNormalAnimatorSet(view),
                hideAnimatorSet(view));
        set.addListener(getEndListener(listener, view, null));
        set.start();
    }

    @SuppressWarnings("PMD.UselessParentheses")
    public static void likeAnimationWithTranslate(@DrawableRes int icon,
                                                  final ImageView view, final View endView,
                                                  final OnFinishListener listener) {
        beforeAnimation(view, endView, icon);
        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.playSequentially(
                showAnimatorSet(view),
                toNormalAnimatorSet(view));
        //это вынесено в отдельный listener потому, что endView не успевает измерится,
        //а для этой анимации нужные его высота и ширина
        scaleSet.addListener(new OnEndAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorSet hideSet = translateHideAnimatorSet(view, endView);
                hideSet.addListener(getEndListener(listener, view, endView));
                hideSet.start();
            }
        });
        scaleSet.start();
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

    private static void beforeAnimation(ImageView view, View endView, int icon) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            view.setTranslationX(0);
            view.setTranslationY(0);
            view.setImageResource(icon);
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        if (endView != null) {
            endView.setAlpha(0f);
            endView.setVisibility(View.VISIBLE);
        }
    }

    private static OnEndAnimatorListener getEndListener(final OnFinishListener listener,
                                                        final ImageView view, final View endView) {
        return new OnEndAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.call();
                }
                if (view != null) {
                    view.setVisibility(View.GONE);
                    view.setImageDrawable(null);
                    view.setLayerType(View.LAYER_TYPE_NONE, null);
                }
                if (endView != null) {
                    endView.setAlpha(1f);
                }
            }
        };
    }

    private static AnimatorSet showAnimatorSet(View view) {
        AnimatorSet set = new AnimatorSet();
        ValueAnimator showAlpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ValueAnimator showScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.2f, 1.4f);
        ValueAnimator showScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.2f, 1.4f);
        set.play(showAlpha).with(showScaleX).with(showScaleY);
        set.setDuration(DURATION_LIKE_ANIMATION);
        return set;
    }

    private static AnimatorSet toNormalAnimatorSet(View view) {
        AnimatorSet set = new AnimatorSet();
        ValueAnimator toNormalScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.4f, 1f);
        ValueAnimator toNormalScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.4f, 1f);
        set.play(toNormalScaleX).with(toNormalScaleY);
        set.setDuration(DURATION_LIKE_ANIMATION / 2);
        return set;
    }

    private static AnimatorSet hideAnimatorSet(View view) {
        long delay = DURATION_LIKE_ANIMATION * 2;
        AnimatorSet set = new AnimatorSet();
        ValueAnimator hideAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        ValueAnimator hideScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.2f);
        ValueAnimator hideScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.2f);
        set.play(hideAlpha).with(hideScaleX).with(hideScaleY).after(delay);
        set.setDuration(DURATION_LIKE_ANIMATION);
        return set;
    }

    private static AnimatorSet translateHideAnimatorSet(View view, View endView) {
        long delay = DURATION_LIKE_ANIMATION * 2;
        AnimatorSet set = new AnimatorSet();
        ValueAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, getScaleX(view, endView));
        ValueAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, getScaleY(view, endView));
        ObjectAnimator translateX = ObjectAnimator.ofFloat(view, "translationX", 0, getX(view, endView));
        ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "translationY", 0, getY(view, endView));
        set.play(scaleX).with(scaleY).with(translateX).with(translateY).after(delay);
        set.setDuration(DURATION_LIKE_ANIMATION);
        return set;
    }

    private static float getScaleX(View view, View endView) {
        return ((float) endView.getWidth()) / (view.getWidth() * 2);
    }

    private static float getScaleY(View view, View endView) {
        return ((float) endView.getHeight()) / (view.getHeight() * 2);
    }

    private static float getX(View view, View endView) {
        return endView.getLeft()
                + ((View) endView.getParent()).getLeft()
                - ((View) view.getParent()).getWidth() / 2
                + endView.getWidth() / 2;
    }

    private static float getY(View view, View endView) {
        return endView.getTop()
                + ((View) endView.getParent()).getTop()
                - ((View) view.getParent()).getHeight() / 2
                + endView.getHeight() / 2;
    }

    public interface OnFinishListener {
        void call();
    }
}
