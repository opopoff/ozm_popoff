package com.ozm.rocks.util;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.florent37.materialimageloading.MaterialImageLoading;

public class FadeImageLoading {

    private static final int DURATION = 1000;

    private void FadeImageLoading() {
        //nothing;
    }

    public static void animate(@NonNull ImageView imageView) {
        MaterialImageLoading.animate(imageView).setDuration(1000).start();
    }

}
