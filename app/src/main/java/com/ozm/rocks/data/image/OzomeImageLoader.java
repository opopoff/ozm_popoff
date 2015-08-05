package com.ozm.rocks.data.image;

import android.app.Application;
import android.content.Context;
import android.support.annotation.IntDef;
import android.widget.ImageView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ozm.rocks.ApplicationScope;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import timber.log.Timber;

@ApplicationScope
public class OzomeImageLoader {

    public static final int IMAGE = 1;
    public static final int GIF = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IMAGE, GIF})
    public @interface Type {
    }

    private final Context context;
    private final Picasso picasso;
    private final Ion ion;

    @Inject
    public OzomeImageLoader(Application application, Ion ion, Picasso picasso) {
        this.context = application.getApplicationContext();
        this.picasso = picasso;
        this.ion = ion;
    }

    public void fetch(@Type int type, String url) {
        if (type == IMAGE) {
            picasso.load(url).fetch();
        }
    }

    public void load(int position, @Type int type, String url, ImageView target, final Listener listener) {
        Timber.d("OzomeImageLoader: position=%d, type=%s, url=%s", position, type == IMAGE ? "IMAGE" : "GIF", url);
        load(type, url, target, listener);
    }
        public void load(@Type int type, String url, ImageView target, final Listener listener) {
        if (type == GIF) {
            Ion.with(context).load(url).withBitmap().intoImageView(target).setCallback(
                    new FutureCallback<ImageView>() {
                        @Override
                        public void onCompleted(Exception e, ImageView result) {
                            if (listener != null) {
                                if (e == null) {
                                    listener.onSuccess();
                                } else {
                                    listener.onError();
                                }
                            }
                        }
                    });
        } else {
            picasso.load(url).noFade().into(target, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        }

                        @Override
                        public void onError() {
                            if (listener != null) {
                                listener.onError();
                            }
                        }
                    }
            );
        }
    }

    public static interface Listener {
        void onSuccess();

        void onError();
    }
}
