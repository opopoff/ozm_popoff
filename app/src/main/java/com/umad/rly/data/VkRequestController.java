package com.umad.rly.data;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKList;

public class VkRequestController {

    private static final int REQUEST_ATTEMPTS = 3;
    private static final String COUNT_DIALOGS = "10";

    private VkRequestController() {
        // nothing;
    }

    public static void getUserProfile(VKRequest.VKRequestListener listener) {
        VKApi.users().get(VKParameters
                .from(VKApiConst.FIELDS, "id,first_name,last_name"))
                .executeWithListener(listener);
    }

    public static void getUsersInfo(VKList<VKApiDialog> vkApiDialogs, VKRequest.VKRequestListener listener) {
        String users = "";
        if (vkApiDialogs != null && vkApiDialogs.size() > 0) {
            for (VKApiDialog vkApiDialog : vkApiDialogs) {
                users = users + vkApiDialog.message.user_id + ",";
            }
            VKRequest userRequest = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                    users, VKApiConst.FIELDS, "photo_100"));
            userRequest.attempts = REQUEST_ATTEMPTS;
            userRequest.executeWithListener(listener);
        }
    }

    public static void getDialogs(VKRequest.VKRequestListener listener) {
        VKRequest dialogsRequest = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, COUNT_DIALOGS));
        dialogsRequest.attempts = REQUEST_ATTEMPTS;
        dialogsRequest.executeWithListener(listener);
    }
}
