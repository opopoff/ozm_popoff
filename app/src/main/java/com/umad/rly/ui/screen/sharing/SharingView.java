package com.umad.rly.ui.screen.sharing;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kboyarshinov.autoinflate.AutoInflateLayout;
import com.ozm.R;
import com.umad.rly.base.ComponentFinder;
import com.umad.rly.base.mvp.BaseView;
import com.umad.rly.data.TokenStorage;
import com.umad.rly.data.VkRequestController;
import com.umad.rly.data.analytics.LocalyticsController;
import com.umad.rly.data.api.response.PackageRequest;
import com.umad.rly.data.image.OzomeImageLoader;
import com.umad.rly.data.social.SocialPresenter;
import com.umad.rly.ui.misc.HorizontalListView;
import com.umad.rly.ui.misc.Misc;
import com.umad.rly.util.AnimationTools;
import com.umad.rly.util.DimenTools;
import com.umad.rly.util.PInfo;
import com.umad.rly.util.PackageManagerTools;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;

public class SharingView extends AutoInflateLayout implements BaseView {

    @Inject
    SharingActivity.Presenter presenter;
    @Inject
    OzomeImageLoader ozomeImageLoader;
    @Inject
    Picasso picasso;
    @Inject
    Application application;
    @Inject
    SocialPresenter socialPresenter;
    @Inject
    LocalyticsController localyticsController;
    @Inject
    TokenStorage tokenStorage;

    @InjectView(R.id.sharing_view_header_image)
    protected ImageView headerImage;
    @InjectView(R.id.sharing_view_like)
    protected ImageView likeIcon;
    @InjectView(R.id.sharing_view_vk_container)
    protected FrameLayout vkContainer;
    @InjectView(R.id.sharing_view_vk_list)
    protected HorizontalListView vkList;
    @InjectView(R.id.sharing_view_vk_auth)
    protected View authVk;
    @InjectView(R.id.sharing_header_share_middle_divider)
    protected View middleDivider;
    @InjectView(R.id.sharing_view_vk_header)
    protected View vkHeader;
    @InjectView(R.id.sharing_dialog_header_like)
    protected TextView like;
    @InjectView(R.id.sharing_view_fb_container)
    protected View authFbContainer;
    @InjectView(R.id.sharing_dialog_vk_progress)
    protected ProgressBar vkProgress;
    @InjectView(R.id.sharing_view_vk_list_check)
    protected CheckBox sendLinkToVkCheck;
    @InjectView(R.id.sharing_view_list)
    protected ListView listView;

    private static final String VK_API_USERS_KEY = "VK_API_USERS_KEY";
    private SharingViewAdapter sharingViewAdapter;
    private SharingVkAdapter sharingVkAdapter;
    private VKList<VKApiUser> apiUsers;
    private static final String[] sMyScope = new String[]{
            VKScope.FRIENDS,
            VKScope.PHOTOS,
            VKScope.MESSAGES,
            VKScope.DOCS
    };

    @OnClick(R.id.sharing_dialog_header_like_container)
    protected void likeContainer() {
        presenter.like();
        setLike(!presenter.getImageResponse().liked);
    }

    @OnClick(R.id.sharing_view_vk_auth)
    protected void authVk() {
        localyticsController.setVkAuthorization(LocalyticsController.START);
        VKSdk.login((Activity) getContext(), sMyScope);
    }

    @OnClick(R.id.sharing_view_fb)
    protected void authFB() {
        presenter.shareFB();
    }

    public SharingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            SharingComponent component = ComponentFinder.findActivityComponent(context);
            component.inject(this);
        }
        sharingViewAdapter = new SharingViewAdapter(context);
        sharingVkAdapter = new SharingVkAdapter(context, picasso);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        setHeader();
        listView.setAdapter(sharingViewAdapter);
        vkList.setAdapter(sharingVkAdapter);
        vkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                if (position == sharingVkAdapter.getCount() - 1) {
                    presenter.shareVKAll();
                } else if (sharingVkAdapter.getOnItemClick().onItemClick(view, position)) {
                    if (sendLinkToVkCheck.isChecked()) {
                        localyticsController.setShareOzm(LocalyticsController.VK);
                    }
                    presenter.shareVK(sharingVkAdapter.getItem(position), sendLinkToVkCheck.isChecked());
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        socialPresenter.setVkCallback(vkAccessTokenVKCallback);
    }

    @Override
    protected void onDetachedFromWindow() {
        socialPresenter.setVkCallback(null);
        super.onDetachedFromWindow();
    }

    public void setData(final ArrayList<PInfo> pInfos) {
//        setHeader();
        //set list
        sharingViewAdapter.clear();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == listView.getAdapter().getCount() - 1) {
                    // Hide image;
                    presenter.hide();
                } else if (position == listView.getAdapter().getCount() - 2) {
                    // Share via browser;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(presenter
                            .getImageResponse().url));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    application.startActivity(browserIntent);
                } else if (position == listView.getAdapter().getCount() - 3) {
                    // Copy link to keyboard buffer;
                    ClipboardManager clipboard = (ClipboardManager)
                            application.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", presenter.getImageResponse().url);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(application.getApplicationContext(),
                            getResources().getString(R.string.sharing_view_copy_link_toast),
                            Toast.LENGTH_SHORT).show();
                } else if (position == listView.getAdapter().getCount() - 4) {
                    // Invokes standard dialog sharing;
                    presenter.shareOther();
                } else {
                    // Share via favorite messengers;
                    if (position > 0 && position < sharingViewAdapter.getCount()) {
                        presenter.share(sharingViewAdapter.getItem(position - 1));
                    }
                }
            }
        });
        PInfo pInfo = new PInfo(getResources().getString(R.string.sharing_view_other),
                ((BitmapDrawable) Misc.getDrawable(getResources(), R.drawable.ic_share_other)).getBitmap());
        pInfos.add(pInfo);
        pInfo = new PInfo(getResources().getString(R.string.sharing_view_copy_link),
                ((BitmapDrawable) Misc.getDrawable(getResources(), R.drawable.ic_copy_link)).getBitmap());
        pInfos.add(pInfo);
        pInfo = new PInfo(getResources().getString(R.string.sharing_view_open_in_browser),
                ((BitmapDrawable) Misc.getDrawable(getResources(), R.drawable.ic_open_in_browser)).getBitmap());
        pInfos.add(pInfo);
        pInfo = new PInfo(getResources().getString(R.string.sharing_view_hide),
                ((BitmapDrawable) Misc.getDrawable(getResources(), R.drawable.ic_hide_image)).getBitmap());
        pInfos.add(pInfo);
        sharingViewAdapter.addAll(pInfos);
        sharingViewAdapter.notifyDataSetChanged();
    }

    private void setHeader() {
        //image
        int maxHeight = (int) (((float) DimenTools.displaySize(application).y) * 0.4);
        float aspectScreen = ((float) DimenTools.displaySize(application).x) / maxHeight;
        float aspectImage = ((float) presenter.getImageResponse().width) / presenter.getImageResponse().height;
        if (aspectImage < aspectScreen) {
            headerImage.getLayoutParams().height = maxHeight;
            headerImage.getLayoutParams().width = (int) (presenter.getImageResponse().width
                    * (((float) maxHeight) / presenter.getImageResponse().height));
        } else {
            headerImage.getLayoutParams().height = (int) (presenter.getImageResponse().height
                    * (((float) DimenTools.displaySize(application).x) / presenter.getImageResponse().width));
        }
        ozomeImageLoader.load(presenter.getImageResponse().isGIF ? OzomeImageLoader.GIF : OzomeImageLoader.IMAGE,
                presenter.getImageResponse().url, headerImage, new OzomeImageLoader.Listener() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        if (bytes != null) {
                            GifDrawable gifDrawable = null;
                            try {
                                gifDrawable = new GifDrawable(bytes);
                                headerImage.setImageDrawable(gifDrawable);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!presenter.getImageResponse().liked) {
                    presenter.like();
                    likeAnimation();
                    setLike(!presenter.getImageResponse().liked);
                }
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
        //like
        setLike(presenter.getImageResponse().liked);

        if (authFbContainer.getVisibility() == VISIBLE
                && ((View) vkContainer.getParent()).getVisibility() == VISIBLE) {
            middleDivider.setVisibility(GONE);
        }
        //vk
        if (VKSdk.wakeUpSession(getContext())) {
            middleDivider.setVisibility(VISIBLE);
            vkHeader.setVisibility(VISIBLE);
            authVk.setVisibility(GONE);
            vkProgress.setVisibility(VISIBLE);
            ((ViewGroup) vkList.getParent()).setVisibility(INVISIBLE);
            setVk();
        } else {
            authVk.setVisibility(VISIBLE);
            vkHeader.setVisibility(GONE);
        }
    }

    public void setVisibilityVkFb(ArrayList<PInfo> packages) {
        boolean foundVk = false;
        boolean foundFb = false;
        for (PInfo pInfo : packages) {
            if (pInfo.getPackageName().equals(PackageManagerTools.Messanger.FACEBOOK_MESSANGER.getPackagename())) {
                authFbContainer.setVisibility(VISIBLE);
                foundFb = true;
            } else if (pInfo.getPackageName().equals(PackageManagerTools.Messanger.VKONTAKTE.getPackagename())) {
                ((View) vkContainer.getParent()).setVisibility(VISIBLE);
                foundVk = true;
            }
            if (foundFb && foundVk) {
                break;
            }
        }
    }

    private void setLike(boolean liked) {
        Drawable drawable;
        if (liked) {
            like.setText(getResources().getString(R.string.sharing_view_liked).toUpperCase());
            drawable = Misc.getDrawable(getResources(), R.drawable.ic_favorite_check);
            if (drawable != null) {
                drawable.setColorFilter(getResources().getColor(R.color.sharing_view_header_bg),
                        PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            like.setText(getResources().getString(R.string.sharing_view_not_liked).toUpperCase());
            drawable = Misc.getDrawable(getResources(), R.drawable.ic_favorite_unckeck);
            if (drawable != null) {
                drawable.setColorFilter(getResources().getColor(R.color.sharing_view_header_bg),
                        PorterDuff.Mode.SRC_ATOP);
            }
        }
        like.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private void setVk() {
        if (apiUsers != null) {
            vkProgress.setVisibility(GONE);
            ((ViewGroup) vkList.getParent()).setVisibility(VISIBLE);
//            sharingVkAdapter.clear();
            sharingVkAdapter.addAll(apiUsers);
            sharingVkAdapter.notifyDataSetChanged();
        } else {
            VkRequestController.getDialogs(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    VKApiGetDialogResponse vkResponses = (VKApiGetDialogResponse) response.parsedModel;
                    getUsers(vkResponses.items);
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    setVk();
                }
            });
        }
    }

    public void notifyVkAdapter() {
        sharingVkAdapter.notifyDataSetChanged();
    }

    private void getUsers(final VKList<VKApiDialog> apiVkMessages) {
        VkRequestController.getUsersInfo(apiVkMessages, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                vkProgress.setVisibility(GONE);
                ((ViewGroup) vkList.getParent()).setVisibility(VISIBLE);
                apiUsers = (VKList<VKApiUser>) response.parsedModel;
                sharingVkAdapter.clear();
                sharingVkAdapter.addAll(apiUsers);
                sharingVkAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                getUsers(apiVkMessages);
            }
        });
    }

    public void likeAnimation() {
        AnimationTools.likeAnimation(
                presenter.getImageResponse().liked ? R.drawable.ic_like_empty
                        : R.drawable.ic_star_big, likeIcon, null);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(VK_API_USERS_KEY, apiUsers);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (state instanceof Bundle) {
            apiUsers = ((Bundle) state).getParcelable(VK_API_USERS_KEY);
        }
    }

    private void onSuccessVkToken() {
        localyticsController.setVkAuthorization(LocalyticsController.SUCCESS);
        VkRequestController.getUserProfile(
                new VKRequest.VKRequestListener() {

                    @Override
                    public void onComplete(VKResponse response) {
                        VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                        PackageRequest.VkData vkData = new PackageRequest.VkData(
                                user.id, user.first_name, user.last_name);
                        tokenStorage.setVkData(vkData);
                        presenter.sendPackages(vkData);
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                    }
                });

        middleDivider.setVisibility(VISIBLE);
        vkHeader.setVisibility(VISIBLE);
        authVk.setVisibility(GONE);
        vkProgress.setVisibility(VISIBLE);
        ((ViewGroup) vkList.getParent()).setVisibility(INVISIBLE);
        setVk();
    }


        private VKCallback<VKAccessToken> vkAccessTokenVKCallback = new VKCallback<VKAccessToken>() {
        @Override
        public void onResult(VKAccessToken vkAccessToken) {
            onSuccessVkToken();
        }

        @Override
        public void onError(VKError vkError) {
            Toast.makeText(application, R.string.error_information_repeate_please, Toast.LENGTH_SHORT).show();
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
