package com.ozm.rocks.data.image;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.ozm.rocks.ApplicationScope;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    private final Ion ion;

    @Inject
    public OzomeImageLoader(Application application, Ion ion, Picasso picasso) {
        this.context = application.getApplicationContext();
        this.picasso = picasso;
        this.ion = ion;
        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
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

    public void load(@Type int type, final String url, ImageView target, final Listener listener) {
        target.setImageDrawable(null);
        if (type == GIF) {
            Uri downloadUri = Uri.parse(url);
            final String path = context.getExternalCacheDir()
                    + url.substring(url.lastIndexOf(File.separator) + 1, url.length()) + "_tumb";
            File file = new File(path);
            if (file.exists()) {
                if (listener != null) {
                    listener.onSuccess(getByteArrayFromFile(path));
                }
            } else {
                Uri destinationUri = Uri.parse(path);
                final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                        .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                        .setDownloadListener(new DownloadStatusListener() {
                            @Override
                            public void onDownloadComplete(int id) {
                                if (listener != null) {
                                    listener.onSuccess(getByteArrayFromFile(path));
                                }
                            }

                            @Override

                            public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                                if (listener != null) {
                                    listener.onError();
                                }
                            }

                            @Override
                            public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {

                            }
                        });
                downloadManager.add(downloadRequest);
            }
//            Ion.with(context).load(url).asByteArray().setCallback(new FutureCallback<byte[]>() {
//                @Override
//                public void onCompleted(Exception e, byte[] bytes) {
//                    if (e == null) {
//                        if (listener != null) {
//                            listener.onSuccess(bytes);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onError();
//                        }
//                    }
//                }
//            });
        } else {
            picasso.load(url).noFade().into(target, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (listener != null) {
                                listener.onSuccess(null);
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

    private byte[] getByteArrayFromFile(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(path));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface Listener {
        void onSuccess(byte[] bytes);

        void onError();
    }
}
