package com.umad.wat.data;

import com.umad.wat.util.Strings;
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
        if (vkApiDialogs != null && vkApiDialogs.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (VKApiDialog vkApiDialog : vkApiDialogs) {
                sb.append(vkApiDialog.message.user_id);
                sb.append(Strings.COMMA);
            }
            String users = sb.toString();
            VKParameters photo = VKParameters.from(VKApiConst.USER_IDS, users, VKApiConst.FIELDS, "photo_100");
            VKRequest userRequest = VKApi.users().get(photo);
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
