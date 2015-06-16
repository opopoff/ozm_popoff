package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
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

import com.ozm.R;
import com.ozm.rocks.base.ActivityConnector;
import com.ozm.rocks.data.vk.ApiVkMessage;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.vk.ApiVkDialogResponse;
import com.ozm.rocks.data.vk.ApiVkUserResponse;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.ui.misc.Misc;
import com.ozm.rocks.util.PInfo;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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
    TextView auth;
    @InjectView(R.id.sharing_dialog_vk_container)
    LinearLayout vkContainer;



    @OnClick(R.id.sharing_dialog_header_image)
    public void back() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    @OnClick(R.id.sharing_dialog_vk_auth)
    public void authVk() {
        VKSdk.authorize(VKScope.MESSAGES, VKScope.FRIENDS);
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

    public void openDialog(final ArrayList<PInfo> pInfos, final ImageResponse image, final Picasso picasso) {
        if (mAlertDialog == null || (!mAlertDialog.isShowing())) {
            final Activity activity = getAttachedObject();
            if (activity == null) return;
            resources = activity.getResources();
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            SharingDialogAdapter sharingDialogAdapter = new SharingDialogAdapter(activity);
            final View sharingDialog = layoutInflater.inflate(R.layout.sharing_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
            ButterKnife.inject(this, sharingDialog);
            Drawable drawable = Misc.getDrawable(R.drawable.ic_action_back, resources);
            if (drawable != null) {
                drawable.setColorFilter(activity.getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
            }
            headerImage.setImageDrawable(drawable);
            list.setAdapter(sharingDialogAdapter);
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
                                resources.getString(R.string.sharing_dialog_copy_link_toast),
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
            PInfo pInfo = new PInfo(activity.getResources().getString(R.string.sharing_dialog_hide),
                    Misc.getDrawable(R.drawable.ic_hide, resources));
            pInfos.add(pInfo);
            pInfo = new PInfo(resources.getString(R.string.sharing_dialog_copy_link),
                    Misc.getDrawable(R.drawable.ic_copy, resources));
            pInfos.add(pInfo);
            pInfo = new PInfo(resources.getString(R.string.sharing_dialog_other),
                    Misc.getDrawable(R.drawable.ic_other, resources));
            pInfos.add(pInfo);


            for (int i = 0; i < pInfos.size(); i++) {
                if (i < 3 && i < pInfos.size() - 3) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setImageDrawable(pInfos.get(i).getIcon());
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
                    sharingDialogAdapter.add(pInfos.get(i));
                }
            }
            if (topContainer.getChildCount() == 0) {
                topContainer.setVisibility(View.GONE);
            }
            //vk
            if (VKSdk.wakeUpSession()) {
                auth.setVisibility(View.GONE);
                VKRequest dialogsRequest  = new VKRequest("messages.getDialogs",
                        VKParameters.from(VKApiConst.COUNT, "3"),
                        VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
                dialogsRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        ApiVkDialogResponse vkResponses = (ApiVkDialogResponse) response.parsedModel;
                        for (ApiVkMessage apiVkMessage : vkResponses.dialogs.items) {
                            VKRequest userRequest  = VKApi.users().get(VKParameters.from(
                                    VKApiConst.USER_ID, apiVkMessage.message.user_id,
                                    VKApiConst.FIELDS, "photo_50"));
//                            VKRequest userRequest  = new VKRequest("users.get",
//                                    VKParameters.from(VKApiConst.USER_ID, apiVkMessage.message.user_id,
//                                            VKApiConst.FIELDS, "photo_50"),
//                                    VKRequest.HttpMethod.GET, ApiVkUserResponse.class);
                            userRequest.executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    VKList<VKApiUser> vkApiUserFull = (VKList<VKApiUser>) response.parsedModel;
                                    ImageView imageView = new ImageView(activity);
                                    picasso.load(vkApiUserFull.get(0).photo_50).noFade().into(imageView, null);
                                    vkContainer.addView(imageView);
                                    int padding = vkContainer.getResources().getDimensionPixelSize(
                                            R.dimen.sharing_dialog_top_element_padding);
                                    imageView.setPadding(padding, 0, padding, 0);
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                auth.setVisibility(View.VISIBLE);
            }
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

    public interface SharingDialogCallBack {
        void share(PInfo pInfo, ImageResponse imageResponse);

        void hideImage(ImageResponse imageResponse);

        void other(ImageResponse imageResponse);
    }
}
