package com.ozm.rocks.data;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.Strings;

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

    private static final String DIRECTORY_NAME = "ozome";
    private static final int MAX_FILES_IN_GALLERY = 100;
    private static final int COMPRESS_QUALITY = 100;
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private final Application application;

    @Inject
    public FileService(Application application) {
        this.application = application;
    }

    public String createFile(String urllink) {
        try {
            String path = createDirectory() + Strings.SLASH + getFileName(urllink);
            File dir = createDirectory();
            File file = new File(path);
            if (!file.exists()) {
                if (dir.listFiles().length >= MAX_FILES_IN_GALLERY) {
                    File[] files = dir.listFiles();

                    Arrays.sort(files, new Comparator<File>() {
                        public int compare(File f1, File f2) {
                            return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                        }
                    });
                    boolean isDeleted = files[files.length - 1].delete();
                }
                URL url = new URL(urllink);
                long startTime = System.currentTimeMillis();
                Timber.d(String.format("FileService: download url: %s", url));
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                FileOutputStream outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, outStream);
                outStream.flush();
                outStream.close();
                application.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(path)));
                Timber.d(String.format("FileService: download ready in %d sec to %s",
                        (System.currentTimeMillis() - startTime) / MILLISECONDS_IN_SECOND, path));
            }
            return "complete";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String deleteFile(String urllink) {
        String path = createDirectory() + Strings.SLASH + getFileName(urllink);
        File file = new File(path);
        if (file.exists()) {
            boolean isDeleted = file.delete();
        }
        return "complete";
    }

    public static File createDirectory() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), DIRECTORY_NAME);
        if (!folder.exists()) {
            boolean succes = folder.mkdir();
            Timber.d("Create folder = %b", succes);
        }
        return folder;
    }

    public static String getFileName(String url) {
        String string = url.substring(url.lastIndexOf(File.separator) + 1, url.length());
        try {
            return java.net.URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return string;
        }
    }
}
