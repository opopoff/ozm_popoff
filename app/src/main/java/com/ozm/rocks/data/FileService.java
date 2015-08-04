package com.ozm.rocks.data;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ozm.rocks.ApplicationScope;
import com.ozm.rocks.util.Strings;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Danil on 18.05.2015.
 */
@ApplicationScope
public class FileService {

    private static final String DIRECTORY_NAME = "OZM!";
    private static final int MAX_FILES_IN_GALLERY = 100;
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private final Application application;

    private Future<File> downloading;

    @Inject
    public FileService(Application application) {
        this.application = application;
    }

    public Boolean createFile(String urllink, String fileType, boolean isSharingUrl, boolean isCreateAlbum) {
        try {
            String path = getFullFileName(application, urllink, fileType, isCreateAlbum, isSharingUrl);
            File file = new File(path);
            if (!file.exists()) {
                if (isCreateAlbum) {
                    File dir = createDirectory();
                    if (!isSharingUrl) {
                        if (dir != null && dir.listFiles().length >= MAX_FILES_IN_GALLERY) {
                            File[] files = dir.listFiles();

                            Arrays.sort(files, new Comparator<File>() {
                                public int compare(File f1, File f2) {
                                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                                }
                            });
                            files[files.length - 1].delete();
                        }
                    }
                }
                URL url = new URL(urllink);
                long startTime = System.currentTimeMillis();
                Timber.d(String.format("FileService: download url: %s", url));
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                FileOutputStream outStream = new FileOutputStream(file);
                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    outStream.write(data, 0, count);
                }
                outStream.flush();
                outStream.close();
                if (!isSharingUrl) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    application.sendBroadcast(mediaScanIntent);
                }
                Timber.d(String.format("FileService: download ready in %d sec to %s",
                        (System.currentTimeMillis() - startTime) / MILLISECONDS_IN_SECOND, path));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createFileFromPicasso(Picasso picasso, String url, String fileType, boolean isCreateAlbum) {
        try {
            String path = getFullFileName(application, url, fileType, isCreateAlbum, false);
            File file = new File(path);
            if (!file.exists()) {
                if (isCreateAlbum) {
                    File dir = createDirectory();
                    if (dir != null && dir.listFiles() != null
                            && dir.listFiles().length >= MAX_FILES_IN_GALLERY) {
                        File[] files = dir.listFiles();

                        Arrays.sort(files, new Comparator<File>() {
                            public int compare(File f1, File f2) {
                                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                            }
                        });
                        files[files.length - 1].delete();
                    }
                }
                Bitmap bitmap;
                bitmap = picasso.load(url).get();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                application.sendBroadcast(mediaScanIntent);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createFileFromIon(String url, String fileType, boolean isCreateAlbum) {
        String path = getFullFileName(application, url, fileType, isCreateAlbum, false);
        final File file = new File(path);
        if (!file.exists()) {
            if (isCreateAlbum) {
                File dir = createDirectory();
                if (dir != null && dir.listFiles() != null
                        && dir.listFiles().length >= MAX_FILES_IN_GALLERY) {
                    File[] files = dir.listFiles();

                    Arrays.sort(files, new Comparator<File>() {
                        public int compare(File f1, File f2) {
                            return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                        }
                    });
                    files[files.length - 1].delete();
                }
            }
            downloading = Ion.with(application)
                    .load(url)
                    .write(file)
                    .setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File result) {
                            if (e != null) {
                                return;
                            }
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(file);
                            mediaScanIntent.setData(contentUri);
                            application.sendBroadcast(mediaScanIntent);
                        }
                    });
            for (int i = 0; i < 50; i++){
                if (downloading.isDone() || downloading.isCancelled()){
                    return true;
                }
                try {
                    Thread.sleep(100, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        return true;
    }

    public Boolean deleteFile(String urllink, String fileType, boolean isCreateAlbum, boolean isSharingUrl) {
        String path = getFullFileName(application, urllink, fileType, isCreateAlbum, isSharingUrl);
        File file = new File(path);
        return file.exists() && file.delete();
    }

    public static String getFullFileName(Context context, String url, String fileType,
                                         boolean isCreateAlbum, boolean isSharingUrl) {
        if (isSharingUrl || !isCreateAlbum) {
            return context.getExternalCacheDir() + Strings.SLASH + getFileName(url, fileType, isCreateAlbum);
        } else {
            return createDirectory() + Strings.SLASH + getFileName(url, fileType, true);
        }
    }

    public boolean deleteAllFromGallery() {
        File dir = createDirectory();
        if (dir != null && dir.listFiles() != null){
            for (File file : dir.listFiles()) {
                file.delete();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                application.sendBroadcast(mediaScanIntent);
            }
        }
        return true;
    }

    private static File createDirectory() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), DIRECTORY_NAME);
        if (!folder.exists()) {
            boolean succes = folder.mkdir();
            Timber.d("Create folder = %b", succes);
        }
        return folder;
    }

    private static String getFileName(String url, String fileType, boolean isCreateAlbum) {
        if (!Strings.isBlank(fileType)) {
            url = url + Strings.DOT + fileType.toLowerCase();
        }
        String string = url.substring(url.lastIndexOf(File.separator) + 1, url.length());
        if (!isCreateAlbum) {
            string = "temp_url_" + string;
        }
        try {
            return java.net.URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return string;
        }
    }
}
