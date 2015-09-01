package com.ozm.fun.ui.message;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.ozm.fun.base.ActivityConnector;
import com.ozm.fun.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class NoInternetPresenter extends ActivityConnector<MessageInterface> {

    @Inject
    public NoInternetPresenter() {
    }

    public void showMessage() {
        MessageInterface attachedObject = getAttachedObject();
        if (attachedObject != null) {
            attachedObject.getNoNoInternetView().setVisibility(View.VISIBLE);
        }
    }

    public void showMessageWithTimer() {
        final MessageInterface attachedObject = getAttachedObject();
        if (attachedObject != null) {
            final NoInternetView noInternetView = attachedObject.getNoNoInternetView();
            noInternetView.setVisibility(View.VISIBLE);
            noInternetView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setDuration(500);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            noInternetView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    noInternetView.startAnimation(alphaAnimation);
                }
            }, 5000);
        }
    }

    public void hideMessage() {
        MessageInterface attachedObject = getAttachedObject();
        if (attachedObject != null) {
            attachedObject.getNoNoInternetView().setVisibility(View.GONE);
        }
    }

}
