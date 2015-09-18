package com.umad.wat.data.image;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.ImageView;

import com.umad.wat.ApplicationScope;
import com.umad.wat.data.FileService;
import com.umad.wat.data.TokenStorage;
import com.umad.wat.data.rx.RequestFunction;
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

import pl.droidsonroids.gif.GifDrawable;
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
    public OzomeImageLoader(Application application, Picasso picasso) {
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

    public void load(@Type int type, final String url, ImageView target, final Listener listener) {
        target.setImageDrawable(null);
        //cancel downloading on this view
        Integer downloadId = viewListenerMap.remove(target);
        if (downloadId != null) {
            downloadManager.cancel(downloadId);
        }
        //load
        if (type == GIF) {
            loadGif(target, url, listener, true);
        } else {
            picasso.load(url).noFade().into(target, getListenerForPicasso(listener));
        }
    }

    public void loadGif(final ImageView target, String url, final Listener listener, boolean isVisible) {
        final String path = FileService.getFullFileName(context, url, "gif", tokenStorage.isCreateAlbum(), false);
        File file = new File(path);
        if (file.exists()) {
            trySetGif(path, target, listener);
        } else {
            DownloadRequest downloadRequest = getRequestForThin(url, path, target, listener);
            //if the image should now be on the screen, set the priority HIGHT, else LOW
            downloadRequest.setPriority(isVisible
                    ? DownloadRequest.Priority.HIGH
                    : DownloadRequest.Priority.LOW);
            viewListenerMap.put(target, downloadManager.add(downloadRequest));
        }
    }

    private byte[] getByteArrayFromFile(String path) throws IOException {
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
    }

    private void trySetGif(String path, ImageView target, Listener listener) {
        try {
            byte[] bytes = getByteArrayFromFile(path);
            if (bytes != null && target != null) {
                GifDrawable gifDrawable;
                gifDrawable = new GifDrawable(bytes);
                target.setImageDrawable(gifDrawable);
                onSuccess(listener);
            }
        } catch (IOException e) {
            onError(listener);
            e.printStackTrace();
        }
    }

    private void onSuccess(Listener listener) {
        if (listener != null) {
            listener.onSuccess();
        }
    }

    private void onError(Listener listener) {
        if (listener != null) {
            listener.onError();
        }
    }

    private Callback getListenerForPicasso(final Listener listener) {
        return new Callback() {
            @Override
            public void onSuccess() {
                OzomeImageLoader.this.onSuccess(listener);
            }

            @Override
            public void onError() {
                OzomeImageLoader.this.onError(listener);
            }
        };
    }

    private DownloadRequest getRequestForThin(final String url, final String path,
                                              final ImageView target, final Listener listener) {
        Uri downloadUri = Uri.parse(url);
        Uri destinationUri = Uri.parse(path);
        return new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {
                        trySetGif(path, target, listener);
                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                        onError(listener);
                    }

                    @Override
                    public void onProgress(int id, long totalBytes, long downloadedBytes,
                                           int progress) {
                    }
                });
    }

    public interface Listener {
        void onSuccess();

        void onError();
    }
}
