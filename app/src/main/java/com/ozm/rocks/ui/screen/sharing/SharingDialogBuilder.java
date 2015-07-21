package com.ozm.rocks.ui.screen.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.ozm.R;
import com.ozm.rocks.base.ActivityConnector;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.social.dialog.ApiVkMessage;
import com.ozm.rocks.data.social.SocialPresenter;
import com.ozm.rocks.data.social.VkInterface;
import com.ozm.rocks.ApplicationScope;
import com.ozm.rocks.ui.misc.Misc;
import com.ozm.rocks.util.PInfo;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

@ApplicationScope
public class SharingDialogBuilder extends ActivityConnector<Activity> {

    @InjectView(R.id.sharing_dialog_header_text)
    TextView headerText;
    @InjectView(R.id.sharing_dialog_header_image)
    ImageView headerImage;
    @InjectView(R.id.sharing_dialog_top)
    LinearLayout topContainer;
    @InjectView(R.id.sharing_dialog_list)
    ListView list;
    @InjectView(R.id.sharing_dialog_vk_auth)
    TextView vkAuth;
    @InjectView(R.id.sharing_dialog_vk_container)
    LinearLayout vkContainer;
    @InjectView(R.id.sharing_dialog_facebook_auth)
    TextView facebookAuth;
    @InjectView(R.id.sharing_dialog_facebook_container)
    LinearLayout facebookContainer;

    private Activity activity;
    private Picasso picasso;
    private ImageResponse image;
    private SharingService sharingService;
    private SocialPresenter socialPresenter;


    @OnClick(R.id.sharing_dialog_header_image)
    public void back() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    @OnClick(R.id.sharing_dialog_vk_auth)
    public void authVk() {
        VKSdk.authorize(VKScope.MESSAGES, VKScope.FRIENDS, VKScope.PHOTOS);
    }

    @OnClick(R.id.sharing_dialog_facebook_auth)
    public void authFacebook() {
        LoginManager.getInstance().registerCallback(socialPresenter.getFBCallbackManager(),
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookAuth.setVisibility(View.GONE);
                getFriends();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Timber.e("Facebook Error: %s", e.toString());
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(activity, Collections.singletonList("user_friends"));
    }

    @Nullable
    private
    SharingDialogCallBack mCallBack;
    @Nullable
    private AlertDialog mAlertDialog;
    private Resources resources;

    @Inject
    public SharingDialogBuilder() {

    }

    public void setCallback(SharingDialogCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void openDialog(final ArrayList<PInfo> pInfos, final ImageResponse image, final Picasso picasso,
                           SocialPresenter socialPresenter, SharingService sharingService) {
        if (mAlertDialog == null || (!mAlertDialog.isShowing())) {
            final Activity activity = getAttachedObject();
            if (activity == null) return;
            this.socialPresenter = socialPresenter;
            socialPresenter.setVkInterface(vkInterface);
            this.activity = activity;
            this.image = image;
            this.picasso = picasso;
            this.sharingService = sharingService;
            resources = activity.getResources();
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            SharingViewAdapter sharingViewAdapter = new SharingViewAdapter(activity);
            final View sharingDialog = layoutInflater.inflate(R.layout.sharing_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
            ButterKnife.inject(this, sharingDialog);
            Drawable drawable = Misc.getDrawable(R.drawable.ic_action_back, resources);
            if (drawable != null) {
                drawable.setColorFilter(activity.getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
            }
            headerImage.setImageDrawable(drawable);
            list.setAdapter(sharingViewAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == list.getAdapter().getCount() - 3) {
                        if (mCallBack != null && mAlertDialog != null) {
                            mCallBack.hideImage(image);
                            mAlertDialog.dismiss();
                        }
                    } else if (position == list.getAdapter().getCount() - 2) {
                        ClipboardManager clipboard = (ClipboardManager)
                                activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", image.url);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(activity.getApplicationContext(),
                                resources.getString(R.string.sharing_view_copy_link_toast),
                                Toast.LENGTH_SHORT).show();
                        if (mAlertDialog != null) {
                            mAlertDialog.dismiss();
                        }
                    } else if (position == list.getAdapter().getCount() - 1) {
                        if (mCallBack != null && mAlertDialog != null) {
                            mCallBack.other(image);
                            mAlertDialog.dismiss();
                        }
                    } else if (mCallBack != null && mAlertDialog != null) {
                        mCallBack.share(pInfos.get(position + 3), image);
                        mAlertDialog.dismiss();
                    }
                }
            });
            PInfo pInfo = new PInfo(activity.getResources().getString(R.string.sharing_view_hide),
                    ((BitmapDrawable) Misc.getDrawable(R.drawable.ic_hide, resources)).getBitmap());
            pInfos.add(pInfo);
            pInfo = new PInfo(resources.getString(R.string.sharing_view_copy_link),
                    ((BitmapDrawable) Misc.getDrawable(R.drawable.ic_copy, resources)).getBitmap());
            pInfos.add(pInfo);
            pInfo = new PInfo(resources.getString(R.string.sharing_view_other), null);
            pInfos.add(pInfo);

            for (int i = 0; i < pInfos.size(); i++) {
                if (i < 3 && i < pInfos.size() - 3) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setImageBitmap(pInfos.get(i).getIcon());
                    topContainer.addView(imageView);
                    int padding = topContainer.getResources().getDimensionPixelSize(
                            R.dimen.sharing_dialog_top_element_padding);
                    imageView.setPadding(padding, 0, padding, 0);
                    final int finalI = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallBack != null && mAlertDialog != null) {
                                mCallBack.share(pInfos.get(finalI), image);
                                mAlertDialog.dismiss();
                            }
                        }
                    });
                } else {
                    sharingViewAdapter.add(pInfos.get(i));
                }
            }
            if (topContainer.getChildCount() == 0) {
                topContainer.setVisibility(View.GONE);
            }
            //vk
            vk();
            builder.setView(sharingDialog);
            mAlertDialog = builder.create();
            mAlertDialog.show();
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.8);
            int height = WindowManager.LayoutParams.WRAP_CONTENT;
            mAlertDialog.getWindow().setLayout(width, height);
        }
    }

    private void vk() {
        if (VKSdk.wakeUpSession()) {
            vkAuth.setVisibility(View.GONE);
            getDialogs();
        } else {
            vkAuth.setVisibility(View.VISIBLE);
        }
    }

    private void getDialogs() {
//        sharingService.vkGetDialogs(new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                super.onComplete(response);
//                ApiVkDialogResponse vkResponses = (ApiVkDialogResponse) response.parsedModel;
//                for (ApiVkMessage apiVkMessage : vkResponses.dialogs.items) {
//                    getUser(apiVkMessage);
//                }
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe();
//        VKRequest dialogsRequest = new VKRequest("messages.getDialogs",
//                VKParameters.from(VKApiConst.COUNT, "3"),
//                VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
//        dialogsRequest.executeWithListener(new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                super.onComplete(response);
//                ApiVkDialogResponse vkResponses = (ApiVkDialogResponse) response.parsedModel;
//                for (ApiVkMessage apiVkMessage : vkResponses.dialogs.items) {
//                    getUser(apiVkMessage);
//                }
//            }
//        });
    }

    private void getUser(ApiVkMessage apiVkMessage) {
        VKRequest userRequest = VKApi.users().get(VKParameters.from(
                VKApiConst.USER_ID, apiVkMessage.message.user_id,
                VKApiConst.FIELDS, "photo_50"));
        userRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final VKList<VKApiUser> apiUsers = (VKList<VKApiUser>) response.parsedModel;
                ImageView imageView = new ImageView(activity);
                picasso.load(apiUsers.get(0).photo_50).noFade().into(imageView, null);
                vkContainer.addView(imageView);
                int padding = vkContainer.getResources().getDimensionPixelSize(
                        R.dimen.sharing_dialog_top_element_padding);
                imageView.setPadding(padding, 0, padding, 0);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null && mAlertDialog != null) {
                            mCallBack.shareVK(image, apiUsers.get(0), new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    Toast.makeText(activity, "Отправлено", Toast.LENGTH_SHORT).show();
                                }
                            });
                            mAlertDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    VkInterface vkInterface = new VkInterface() {
        @Override
        public void onCaptchaError(VKError vkError) {
        }

        @Override
        public void onTokenExpired(VKAccessToken vkAccessToken) {
        }

        @Override
        public void onAccessDenied(VKError vkError) {
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            if (mAlertDialog != null) {
                vkAuth.setVisibility(View.GONE);
                getDialogs();
            }
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            if (mAlertDialog != null) {
                vkAuth.setVisibility(View.GONE);
                getDialogs();
            }
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            if (mAlertDialog != null) {
                vkAuth.setVisibility(View.GONE);
                getDialogs();
            }
        }
    };

    private void facebook() {
//        if (.wakeUpSession()) {
//            vkAuth.setVisibility(View.GONE);
//            getDialogs();
//        } else {
//            vkAuth.setVisibility(View.VISIBLE);
//        }
    }

    private void getFriends() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final String[] userId = {""};
        GraphRequestBatch graphRequests = new GraphRequestBatch(
                GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject jsonObject,
                                    GraphResponse response) {
                                userId[0] = jsonObject.optString("id");
                            }
                        }),
                GraphRequest.newGraphPathRequest(
                        accessToken, "/" + userId[0] + "/friendlists",
        new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                int i = 0;
            }
        }));
        graphRequests.executeAsync();
    }


    public interface SharingDialogCallBack {
        void share(PInfo pInfo, ImageResponse imageResponse);

        void shareVK(ImageResponse imageResponse, VKApiUser user, VKRequest.VKRequestListener vkRequestListener);

        void hideImage(ImageResponse imageResponse);

        void other(ImageResponse imageResponse);
    }
}