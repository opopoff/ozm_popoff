package com.ozm.rocks.data.image;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.ozm.rocks.ApplicationScope;
import com.ozm.rocks.data.FileService;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.data.rx.RequestFunction;
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
import java.util.Map;
import java.util.WeakHashMap;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@ApplicationScope
public class OzomeImageLoader {

    @Inject
    TokenStorage tokenStorage;
    public static final int IMAGE = 1;
    public static final int GIF = 2;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IMAGE, GIF})
    public @interface Type {
    }

    private final Context context;
    private final Picasso picasso;
    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 8;

    Map<View, Integer> viewListenerMap = new WeakHashMap<>();

    @Inject
    public OzomeImageLoader(Application application, Ion ion, Picasso picasso) {
        this.context = application.getApplicationContext();
        this.picasso = picasso;
        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
    }

    public void fetch(@Type int type, String url) {
        if (type == IMAGE) {
            picasso.load(url).fetch();
        } else if (type == GIF) {
            loadGif(null, url, null, false);
        }
    }

    public void load(int position, @Type int type, String url, ImageView target, final Listener listener) {
        Timber.d("OzomeImageLoader: position=%d, type=%s, url=%s", position, type == IMAGE ? "IMAGE" : "GIF", url);
        load(type, url, target, listener);
    }

    public void load(@Type int type, final String url, ImageView target, final Listener listener) {
        target.setImageDrawable(null);
        if (type == GIF) {
            loadGif(target, url, listener, true);
        } else {
            Integer downloadId = viewListenerMap.remove(target);
            if (downloadId != null) {
                downloadManager.cancel(downloadId);
            }
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

    public void loadGif(ImageView view, String url, final Listener listener, boolean isVisible) {
        Uri downloadUri = Uri.parse(url);
        final String path = FileService.getFullFileName(context, url, "gif", tokenStorage.isCreateAlbum(), false);
        File file = new File(path);
        if (file.exists()) {
            Observable.create(new RequestFunction<byte[]>() {
                @Override
                protected byte[] request() {
                    return getByteArrayFromFile(path);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<byte[]>() {
                        @Override
                        public void call(byte[] bytes) {
                            if (listener != null && bytes != null) {
                                listener.onSuccess(bytes);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (listener != null) {
                                listener.onError();
                            }
                        }
                    });
        } else {
            Uri destinationUri = Uri.parse(path);
            final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                    .setDestinationURI(destinationUri)
                    .setDownloadListener(new DownloadStatusListener() {
                        @Override
                        public void onDownloadComplete(int id) {
                            if (listener != null) {
                                Timber.d("OzomeImageLoader gif: download");
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
            if (view != null) {
                Integer downloadId = viewListenerMap.remove(view);
                if (downloadId != null) {
                    downloadManager.cancel(downloadId);
                }
            }
            if (isVisible) {
                downloadRequest.setPriority(DownloadRequest.Priority.HIGH);
            } else {
                downloadRequest.setPriority(DownloadRequest.Priority.LOW);
            }
            viewListenerMap.put(view, downloadManager.add(downloadRequest));
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
            buffer.close();
            fileInputStream.close();
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
