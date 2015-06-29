package com.ozm.rocks.data;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.Strings;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private final Application application;

    @Inject
    public FileService(Application application) {
        this.application = application;
    }

    public Boolean createFile(String urllink, boolean isSharingUrl, boolean isCreateAlbum) {
        try {
            String path = getFullFileName(application,urllink, isCreateAlbum, isSharingUrl);
            File dir = createDirectory();
            File file = new File(path);
            if (!file.exists()) {
                if (!isSharingUrl) {
                    if (dir.listFiles().length >= MAX_FILES_IN_GALLERY) {
                        File[] files = dir.listFiles();

                        Arrays.sort(files, new Comparator<File>() {
                            public int compare(File f1, File f2) {
                                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                            }
                        });
                        files[files.length - 1].delete();
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createFileFromBitmap(Picasso picasso, String url, boolean isCreateAlbum) {
        try {
            String path = getFullFileName(application, url, isCreateAlbum, false);
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
                    files[files.length - 1].delete();
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

    public Boolean deleteFile(String urllink, boolean isCreateAlbum, boolean isSharingUrl) {
        String path = getFullFileName(application, urllink, isCreateAlbum, isSharingUrl);
        File file = new File(path);
        return file.exists() && file.delete();
    }

    public static String getFullFileName(Context context, String url, boolean isCreateAlbum, boolean isSharingUrl) {
        if (isSharingUrl || !isCreateAlbum) {
            return context.getExternalCacheDir() + Strings.SLASH + getFileName(url, isCreateAlbum);
        } else {
            return createDirectory() + Strings.SLASH + getFileName(url, true);
        }
    }

    public boolean deleteAllFromGallery(){
        File dir = createDirectory();
        for(File file : dir.listFiles()){
            file.delete();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            application.sendBroadcast(mediaScanIntent);
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

    private static String getFileName(String url, boolean isCreateAlbum) {
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
