package com.ozm.rocks.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chandru on 31-03-2015.
 */
public class BitmapTools {

    public static Bitmap changeImageColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }


    public static Drawable covertBitmapToDrawable(Context context, Bitmap bitmap) {
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return d;
    }

    public static Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static boolean resizeImage(@NonNull String pathToFile, long size) {
        boolean isResize = false;
        Bitmap bitmap = decodeSampledBitmapFile(pathToFile, size);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pathToFile);
            isResize = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);   // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isResize;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
        return resizeBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        return calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
    }

    public static int calculateInSampleSize(int currentWidth, int currentHeight, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = currentHeight;
        final int width = currentWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            int scale = 1;
            while (width / scale / 2 >= reqWidth && height / scale / 2 >= reqHeight) {
                scale *= 2;
            }
            inSampleSize = scale;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFile(String pathToFile, long size) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToFile, options);

        double heapSize = options.outWidth *  options.outHeight * 4;
        double quality = 1.00d;

        while (heapSize >= size) {
            quality = quality - 0.01d;
            heapSize = options.outWidth * options.outHeight * 4 * quality * quality;
        }

        int reqWidth = (int) (options.outWidth * quality);
        int reqHeight = (int) (options.outHeight * quality);

        // Calculate inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        return BitmapFactory.decodeFile(pathToFile, o2);
    }
}