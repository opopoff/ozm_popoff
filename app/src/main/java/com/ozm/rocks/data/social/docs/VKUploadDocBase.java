package com.ozm.rocks.data.social.docs;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.ozm.rocks.data.rx.RequestFunction;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKAbstractOperation;
import com.vk.sdk.api.httpClient.VKHttpClient;
import com.vk.sdk.api.httpClient.VKJsonOperation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Danil on 27.06.2015.
 */

public abstract class VKUploadDocBase extends VKRequest {
    private static final long serialVersionUID = -4566961568409572159L;
    protected long mAlbumId;
    protected long mGroupId;
    protected long mUserId;
    protected File[] mFiles;
    private static final String VK_BOUNDARY = "Boundary(======VK_SDK_%d======)";

    protected abstract VKRequest getServerRequest();

    protected abstract VKRequest getSaveRequest(JSONObject var1);

    public VKUploadDocBase() {
        super((String) null);
    }

    public VKAbstractOperation getOperation() {
        return new VKUploadDocOperation();
    }

    protected class VKUploadDocOperation extends VKAbstractOperation {
        protected VKAbstractOperation lastOperation;

        protected VKUploadDocOperation() {
        }

        public void start() {
            final VKRequestListener originalListener = VKUploadDocBase.this.requestListener;
            VKUploadDocBase.this.requestListener = new VKRequestListener() {
                public void onComplete(VKResponse response) {
                    VKUploadDocOperation.this.setState(VKOperationState.Finished);
                    response.request = VKUploadDocBase.this;
                    if (originalListener != null) {
                        originalListener.onComplete(response);
                    }

                }

                public void onError(VKError error) {
                    VKUploadDocOperation.this.setState(VKOperationState.Finished);
                    error.request = VKUploadDocBase.this;
                    if (originalListener != null) {
                        originalListener.onError(error);
                    }

                }

                public void onProgress(VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    if (originalListener != null) {
                        originalListener.onProgress(progressType, bytesLoaded, bytesTotal);
                    }

                }
            };
            this.setState(VKOperationState.Executing);
            VKRequest serverRequest = VKUploadDocBase.this.getServerRequest();
            serverRequest.setRequestListener(new VKRequestListener() {
                public void onComplete(final VKResponse response) {
                    Observable.create(new RequestFunction<String>() {
                        @Override
                        protected String request() {
                            String response2 = null;
                            try {
                                File file = VKUploadDocBase.this.mFiles[0];
                                FileInputStream fileInputStream = new FileInputStream(file);
                                String attachmentName = "file";
                                String attachmentFileName = "file.gif";
                                String crlf = "\r\n";
                                String twoHyphens = "--";

                                HttpURLConnection httpUrlConnection = null;
                                URL url = new URL(response.json.getJSONObject("response").getString("upload_url"));
                                httpUrlConnection = (HttpURLConnection) url.openConnection();
                                httpUrlConnection.setUseCaches(false);
                                httpUrlConnection.setDoOutput(true);

                                httpUrlConnection.setRequestMethod("POST");
                                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                                httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                                httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + VK_BOUNDARY);

                                DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());

                                request.writeBytes(twoHyphens + VK_BOUNDARY + crlf);
                                request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName
                                        + "\";filename=\"" + attachmentFileName + "\"" + crlf);
                                request.writeBytes(crlf);

                                int bytesAvailable = fileInputStream.available();
                                int maxBufferSize = 1024 * 1024;
                                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                byte[] buffer = new byte[bufferSize];

                                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                                while (bytesRead > 0) {
                                    request.write(buffer, 0, bufferSize);
                                    bytesAvailable = fileInputStream.available();
                                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                                }

                                request.writeBytes(crlf);
                                request.writeBytes(twoHyphens + VK_BOUNDARY + twoHyphens + crlf);
                                request.flush();
                                request.close();
                                InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());

                                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                                String line = "";
                                StringBuilder stringBuilder = new StringBuilder();
                                while ((line = responseStreamReader.readLine()) != null) {
                                    stringBuilder.append(line).append("\n");
                                }
                                responseStreamReader.close();

                                response2 = stringBuilder.toString();
                                responseStream.close();
                                httpUrlConnection.disconnect();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            return response2;
                        }
                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(s);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    VKRequest saveRequest = VKUploadDocBase.this.getSaveRequest(jsonObject);
                                    saveRequest.setRequestListener(new VKRequestListener() {
                                        public void onComplete(VKResponse response) {
                                            VKUploadDocBase.this.requestListener.onComplete(response);
                                            VKUploadDocOperation.this.setState(VKOperationState.Finished);
                                        }

                                        public void onError(VKError error) {
                                            VKUploadDocBase.this.requestListener.onError(error);
                                        }
                                    });
                                    VKUploadDocOperation.this.lastOperation = saveRequest.getOperation();
                                    VKHttpClient.enqueueOperation(VKUploadDocOperation.this.lastOperation);
                                }
                            });
                }

                public void onError(VKError error) {
                    if (VKUploadDocBase.this.requestListener != null) {
                        VKUploadDocBase.this.requestListener.onError(error);
                    }

                }
            });
            this.lastOperation = serverRequest.getOperation();
            VKHttpClient.enqueueOperation(this.lastOperation);
        }

        public void cancel() {
            if (this.lastOperation != null) {
                this.lastOperation.cancel();
            }

            super.cancel();
        }

        public void finish() {
            super.finish();
            this.lastOperation = null;
        }
    }
}
