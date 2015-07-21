package com.ozm.rocks.ui.screen.sharing;

import com.ozm.rocks.data.social.dialog.ApiVkDialogResponse;
import com.ozm.rocks.data.social.dialog.ApiVkMessage;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

public class VkRequestController {

    private static final int REQUEST_ATTEMPTS = 10;

    private VkRequestController() {
        // nothing;
    }

    public static void getUserProfile(VKRequest.VKRequestListener listener) {
        VKApi.users().get(VKParameters
                .from(VKApiConst.FIELDS, "id,first_name,last_name"))
                .executeWithListener(listener);
    }

    public static void getUsersInfo(ApiVkMessage[] apiVkMessages, VKRequest.VKRequestListener listener) {
        String users = "";
        for (ApiVkMessage apiVkMessage : apiVkMessages) {
            users = users + apiVkMessage.message.user_id + ",";
        }
        VKRequest userRequest = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                users, VKApiConst.FIELDS, "photo_100"));
        userRequest.attempts = REQUEST_ATTEMPTS;
        userRequest.executeWithListener(listener);
    }

    public static void getDialogs(VKRequest.VKRequestListener listener) {
        VKRequest dialogsRequest = new VKRequest("messages.getDialogs",
                VKParameters.from(VKApiConst.COUNT, "10"),
                VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
        dialogsRequest.attempts = REQUEST_ATTEMPTS;
        dialogsRequest.executeWithListener(listener);

    }
}
