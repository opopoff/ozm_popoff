package com.ozm.rocks.data.social.docs;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKAbstractOperation;
import com.vk.sdk.api.httpClient.VKHttpClient;
import com.vk.sdk.api.httpClient.VKJsonOperation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Danil on 27.06.2015.
 */

public abstract class VKUploadDocBase extends VKRequest {
    private static final long serialVersionUID = -4566961568409572159L;
    protected long mAlbumId;
    protected long mGroupId;
    protected long mUserId;
    protected File[] mFiles;

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
                    if(originalListener != null) {
                        originalListener.onComplete(response);
                    }

                }

                public void onError(VKError error) {
                    VKUploadDocOperation.this.setState(VKOperationState.Finished);
                    error.request = VKUploadDocBase.this;
                    if(originalListener != null) {
                        originalListener.onError(error);
                    }

                }

                public void onProgress(VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    if(originalListener != null) {
                        originalListener.onProgress(progressType, bytesLoaded, bytesTotal);
                    }

                }
            };
            this.setState(VKOperationState.Executing);
            VKRequest serverRequest = VKUploadDocBase.this.getServerRequest();
            serverRequest.setRequestListener(new VKRequestListener() {
                public void onComplete(VKResponse response) {
                    try {
                        VKJsonOperation e = new VKJsonOperation(VKHttpClient.fileUploadRequest(response.json.getJSONObject("response").getString("upload_url"), VKUploadDocBase.this.mFiles));
                        e.setJsonOperationListener(new VKJsonOperation.VKJSONOperationCompleteListener() {
                            public void onComplete(VKJsonOperation operation, JSONObject response) {
                                VKRequest saveRequest = VKUploadDocBase.this.getSaveRequest(response);
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

                            public void onError(VKJsonOperation operation, VKError error) {
                                VKUploadDocBase.this.requestListener.onError(error);
                            }
                        });
                        VKUploadDocOperation.this.lastOperation = e;
                        VKHttpClient.enqueueOperation(VKUploadDocOperation.this.lastOperation);
                    } catch (JSONException var4) {
                        VKError error = new VKError(-104);
                        error.httpError = var4;
                        error.errorMessage = var4.getMessage();
                        VKUploadDocBase.this.requestListener.onError(error);
                    }

                }

                public void onError(VKError error) {
                    if(VKUploadDocBase.this.requestListener != null) {
                        VKUploadDocBase.this.requestListener.onError(error);
                    }

                }
            });
            this.lastOperation = serverRequest.getOperation();
            VKHttpClient.enqueueOperation(this.lastOperation);
        }

        public void cancel() {
            if(this.lastOperation != null) {
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
