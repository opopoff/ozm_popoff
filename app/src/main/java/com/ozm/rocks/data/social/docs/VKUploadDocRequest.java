package com.ozm.rocks.data.social.docs;

import com.ozm.rocks.data.social.dialog.ApiVkDialogResponse;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.util.VKJsonHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Danil on 27.06.2015.
 */
public class VKUploadDocRequest  extends VKUploadDocBase {
    private static final long serialVersionUID = 1L;

    public VKUploadDocRequest(File file) {
        this.mFiles = new File[]{file};
    }

    public VKUploadDocRequest(VKUploadImage image) {
        this.mFiles = new File[]{image.getTmpFile()};
    }

    protected VKRequest getServerRequest() {
//        return VKApi.photos().getMessagesUploadServer();
        return new VKRequest("docs.getWallUploadServer", null,
                VKRequest.HttpMethod.GET, null);
    }

    protected VKRequest getSaveRequest(JSONObject response) {
        try {
//            VKRequest saveRequest = VKApi.photos().saveMessagesPhoto(new VKParameters(VKJsonHelper.toMap(response)));
            VKRequest saveRequest = new VKRequest("docs.save",
                    new VKParameters(VKJsonHelper.toMap(response)), HttpMethod.POST, ApiVkDocsResponse.class);
            return saveRequest;
        } catch (JSONException var4) {
            return null;
        }
    }
}
