package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.koushikdutta.ion.Ion;
import com.ozm.R;
import com.ozm.rocks.base.ComponentFinder;
import com.ozm.rocks.base.mvp.BaseView;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.social.ApiVkDialogResponse;
import com.ozm.rocks.data.social.ApiVkMessage;
import com.ozm.rocks.data.social.SocialPresenter;
import com.ozm.rocks.data.social.VkInterface;
import com.ozm.rocks.ui.categories.LikeHideResult;
import com.ozm.rocks.ui.misc.Misc;
import com.ozm.rocks.util.DimenTools;
import com.ozm.rocks.util.PInfo;
import com.ozm.rocks.util.PackageManagerTools;
import com.ozm.rocks.util.RoundImageTransform;
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

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SharingView extends LinearLayout implements BaseView {

    @Inject
    SharingActivity.Presenter presenter;
    @Inject
    LikeHideResult mLikeHideResult;
    @Inject
    Picasso picasso;
    @Inject
    Application application;
    @Inject
    SocialPresenter socialPresenter;

    @InjectView(R.id.sharing_view_header_image)
    protected ImageView headerImage;
    @InjectView(R.id.sharing_view_list)
    protected ListView list;
    @InjectView(R.id.sharing_view_vk_container)
    protected LinearLayout vkContainer;
    @InjectView(R.id.sharing_view_vk_auth)
    protected View authVk;
    @InjectView(R.id.sharing_dialog_header_like)
    protected TextView like;
    @InjectView(R.id.sharing_view_fb)
    protected TextView authFB;
    @InjectView(R.id.sharing_dialog_vk_progress)
    protected ProgressBar vkProgress;

    @OnClick(R.id.sharing_dialog_header_like_container)
    protected void likeContainer() {
        presenter.like();
        setLike(!imageResponse.liked);
        imageResponse.liked = !imageResponse.liked;
    }

    @OnClick(R.id.sharing_view_vk_auth)
    protected void authVk() {
        VKSdk.authorize(VKScope.MESSAGES, VKScope.FRIENDS, VKScope.PHOTOS);
    }

    @OnClick(R.id.sharing_view_fb)
    protected void authFB() {
        LoginManager.getInstance().registerCallback(socialPresenter.getFBCallbackManager(),
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        presenter.shareFB();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException e) {
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions((Activity) context,
                Collections.singletonList("user_friends"));
    }

    private SharingDialogAdapter sharingDialogAdapter;
    private LayoutInflater inflater;
    private ImageResponse imageResponse;
    private Context context;

    public SharingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            SharingComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        sharingDialogAdapter = new SharingDialogAdapter(context);
        this.context = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflater = (LayoutInflater) application.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout header = (LinearLayout) inflater.inflate(R.layout.sharing_view_header, null);
        ((ListView) findViewById(R.id.sharing_view_list)).addHeaderView(header, null, false);
        ButterKnife.inject(this);
        socialPresenter.setVkInterface(vkInterface);
    }

    public void setData(final ImageResponse image, final ArrayList<PInfo> pInfos) {
        imageResponse = image;
        setHeader(pInfos);
        list.setAdapter(sharingDialogAdapter);
        sharingDialogAdapter.clear();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == list.getAdapter().getCount() - 1) {
                    presenter.hide();
                } else if (position == list.getAdapter().getCount() - 2) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(image.url));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    application.startActivity(browserIntent);
                } else if (position == list.getAdapter().getCount() - 3) {
                    ClipboardManager clipboard = (ClipboardManager)
                            application.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", image.url);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(application.getApplicationContext(),
                            getResources().getString(R.string.sharing_view_copy_link_toast),
                            Toast.LENGTH_SHORT).show();
                } else if (position == list.getAdapter().getCount() - 4) {
                    presenter.shareOther();
                } else {
                    presenter.share(pInfos.get(position));
                }
            }
        });

        PInfo pInfo = new PInfo(getResources().getString(R.string.sharing_view_other),
                Misc.getDrawable(R.drawable.ic_share_other, getResources()));
        pInfos.add(pInfo);
        pInfo = new PInfo(getResources().getString(R.string.sharing_view_copy_link),
                Misc.getDrawable(R.drawable.ic_copy_link, getResources()));
        pInfos.add(pInfo);
        pInfo = new PInfo(getResources().getString(R.string.sharing_view_open_in_browser),
                Misc.getDrawable(R.drawable.ic_open_in_browser, getResources()));
        pInfos.add(pInfo);
        pInfo = new PInfo(getResources().getString(R.string.sharing_view_hide),
                Misc.getDrawable(R.drawable.ic_hide_image, getResources()));
        pInfos.add(pInfo);


        sharingDialogAdapter.addAll(pInfos);
        sharingDialogAdapter.notifyDataSetChanged();
        if (VKSdk.wakeUpSession()) {
            authVk.setVisibility(GONE);
            vkProgress.setVisibility(VISIBLE);
            getDialogs();
        } else {
            authVk.setVisibility(VISIBLE);
        }
    }

    private void setHeader(ArrayList<PInfo> pInfos) {
        headerImage.getLayoutParams().height = (int) (imageResponse.height
                * (((float) DimenTools.displaySize(application).x) / imageResponse.width));
        if (imageResponse.isGIF) {
            Ion.with(getContext()).load(imageResponse.url).withBitmap().fitXY().intoImageView(headerImage);
        } else {
            picasso.load(imageResponse.url).noFade().fit().into(headerImage, null);
        }
        final GestureDetector gestureDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        presenter.like();
                        setLike(!imageResponse.liked);
                        imageResponse.liked = !imageResponse.liked;
                        return true;
                    }
                });

        headerImage.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        setLike(imageResponse.liked);
        for (PInfo pInfo : pInfos) {
            if (pInfo.getPackageName().equals(PackageManagerTools.FB_MESSENGER_PACKAGE)) {
                ((View) authFB.getParent()).setVisibility(VISIBLE);
                break;
            }
        }
        for (PInfo pInfo : pInfos) {
            if (pInfo.getPackageName().equals(PackageManagerTools.VK_PACKAGE)) {
                ((View) vkContainer.getParent()).setVisibility(VISIBLE);
                break;
            }
        }
    }

    private void setLike(boolean liked) {
        Drawable drawable;
        if (liked) {
            like.setText(getResources().getString(R.string.sharing_view_liked).toUpperCase());
            drawable = Misc.getDrawable(R.drawable.ic_favorite_check, getResources());
            if (drawable != null) {
                drawable.setColorFilter(getResources().getColor(R.color.sharing_view_header_bg),
                        PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            like.setText(getResources().getString(R.string.sharing_view_not_liked).toUpperCase());
            drawable = Misc.getDrawable(R.drawable.ic_favorite_unckeck, getResources());
            if (drawable != null) {
                drawable.setColorFilter(getResources().getColor(R.color.sharing_view_header_bg),
                        PorterDuff.Mode.SRC_ATOP);
            }
        }
        like.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private void getDialogs() {
        VKRequest dialogsRequest = new VKRequest("messages.getDialogs",
                VKParameters.from(VKApiConst.COUNT, "4"),
                VKRequest.HttpMethod.GET, ApiVkDialogResponse.class);
        dialogsRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                ApiVkDialogResponse vkResponses = (ApiVkDialogResponse) response.parsedModel;
                getUsers(vkResponses.dialogs.items);
            }
//            }
        });
    }

    private void getUsers(ApiVkMessage[] apiVkMessages) {
        String users = "";
        for (ApiVkMessage apiVkMessage : apiVkMessages) {
            users = users + apiVkMessage.message.user_id + ",";
        }
        VKRequest userRequest = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                users, VKApiConst.FIELDS, "photo_100"));
        userRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                vkProgress.setVisibility(GONE);
                final VKList<VKApiUser> apiUsers = (VKList<VKApiUser>) response.parsedModel;
                for (final VKApiUser vkApiUser : apiUsers) {
                    View view = inflater.inflate(R.layout.sharing_view_vk_item, null);
                    ImageView imageView = ((ImageView) view.findViewById(R.id.sharing_view_vk_item_image));
                    ((TextView) view.findViewById(R.id.sharing_view_vk_item_text)).setText(vkApiUser.first_name);
                    picasso.load(vkApiUser.photo_100).noFade().transform(new RoundImageTransform())
                            .into(imageView, null);
                    vkContainer.addView(view);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.shareVK(vkApiUser, new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    Toast.makeText(application, "Отправлено", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    });
                }
            }
        });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
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
            authVk.setVisibility(View.GONE);
            getDialogs();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            authVk.setVisibility(View.GONE);
            getDialogs();
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            authVk.setVisibility(View.GONE);
            getDialogs();
        }
    };

    @Override
    public void showLoading() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError(Throwable throwable) {

    }
}
