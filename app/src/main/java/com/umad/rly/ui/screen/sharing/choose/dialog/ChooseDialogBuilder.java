package com.umad.rly.ui.screen.sharing.choose.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozm.R;
import com.umad.rly.base.ActivityConnector;
import com.umad.rly.data.api.response.ImageResponse;
import com.umad.rly.ApplicationScope;
import com.umad.rly.util.PInfo;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

@ApplicationScope
public class ChooseDialogBuilder extends ActivityConnector<Activity> {

    @InjectView(R.id.choose_dialog_header_text)
    protected TextView headerText;
    @InjectView(R.id.choose_dialog_header_image)
    protected ImageView headerImage;
    @InjectView(R.id.choose_dialog_grid)
    protected GridView gridView;

    @OnClick(R.id.choose_dialog_header_image)
    public void back() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    @Nullable
    private
    ChooseDialogCallBack mCallBack;
    @Nullable
    private AlertDialog mAlertDialog;
    private ChooseDialogAdapter chooseDialogAdapter;

    @Inject
    public ChooseDialogBuilder() {

    }

    public void setCallback(ChooseDialogCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void openDialog(final ArrayList<PInfo> pInfos, final ImageResponse image) {
        if (mAlertDialog == null || (!mAlertDialog.isShowing())) {
            final Activity activity = getAttachedObject();
            if (activity == null) return;
            chooseDialogAdapter = new ChooseDialogAdapter(activity);
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            final View chooseDialog = layoutInflater.inflate(R.layout.choose_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
            ButterKnife.inject(this, chooseDialog);
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = activity.getResources().getDrawable(
                        R.drawable.ic_action_back, null);
            } else {
                drawable = activity.getResources().getDrawable(
                        R.drawable.ic_action_back);
            }
            if (drawable != null) {
                drawable.setColorFilter(activity.getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
            }
            headerImage.setImageDrawable(drawable);
            gridView.setAdapter(chooseDialogAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mCallBack != null) {
                        mCallBack.share(chooseDialogAdapter.getItem(position), image);
                    }
                    if (mAlertDialog != null) {
                        mAlertDialog.dismiss();
                    }
                }
            });
            chooseDialogAdapter.addAll(pInfos);
            builder.setView(chooseDialog);
            mAlertDialog = builder.create();
            mAlertDialog.show();
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.8); // 80% of screen width as customer wanted;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;
            mAlertDialog.getWindow().setLayout(width, height);
        }
    }

    public interface ChooseDialogCallBack {
        void share(PInfo pInfo, ImageResponse imageResponse);
    }
}
