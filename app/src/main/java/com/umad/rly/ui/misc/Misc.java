package com.umad.rly.ui.misc;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;

public class Misc {
    public static Drawable getDrawable(Resources resources, @DrawableRes int resource){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(resource, null);
        } else {
            return resources.getDrawable(resource);
        }
    }

    public static BitmapData getBitmapData(Resources resources, @DrawableRes int drawableResId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawableResId, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        return new BitmapData(imageHeight, imageWidth, imageType);
    }

    public static class BitmapData {
        public final int imageHeight;
        public final int imageWidth;
        public final String imageType;

        public BitmapData(int imageHeight, int imageWidth, String imageType) {
            this.imageHeight = imageHeight;
            this.imageWidth = imageWidth;
            this.imageType = imageType;
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromResourceByMinSide(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapData bitmapData = getBitmapData(res, resId);
        int targetHeight = bitmapData.imageHeight;
        int targetWidth = bitmapData.imageWidth;
        if (targetHeight > reqHeight && targetWidth > reqWidth) {
            if (reqWidth <= reqHeight) {
                targetHeight = targetHeight * reqWidth / targetWidth;
                targetWidth = reqWidth;
            } else {
                targetWidth = targetWidth * reqHeight / targetHeight;
                targetHeight = reqHeight;
            }
        } else if (targetHeight < reqHeight && targetWidth < reqWidth) {
            if (reqWidth >= reqHeight) {
                targetHeight = targetHeight * reqWidth / targetWidth;
            } else {
                targetWidth = targetWidth * reqHeight / targetHeight;
            }
        } else if (targetHeight >= reqHeight && targetWidth < reqWidth) {
            targetWidth = targetWidth * reqHeight / targetHeight;
            targetHeight = reqHeight;
        } else if (targetHeight < reqHeight && targetWidth >= reqWidth) {
            targetHeight = targetHeight * reqWidth/ targetWidth;
            targetWidth = reqWidth;
        }
        return decodeSampledBitmapFromResource(res, resId, targetHeight, targetWidth);
    }
}
