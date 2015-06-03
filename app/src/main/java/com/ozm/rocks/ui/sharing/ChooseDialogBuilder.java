package com.ozm.rocks.ui.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozm.R;
import com.ozm.rocks.base.ActivityConnector;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.PInfo;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

@ApplicationScope
public class ChooseDialogBuilder extends ActivityConnector<Activity> {

    @InjectView(R.id.choose_dialog_header_text)
    TextView headerText;
    @InjectView(R.id.choose_dialog_header_image)
    ImageView headerImage;
    @InjectView(R.id.choose_dialog_grid)
    GridView gridView;

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
            View chooseDialog = layoutInflater.inflate(R.layout.main_choose_dialog, null);
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
            mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Point size = new Point();
                    activity.getWindowManager().getDefaultDisplay().getSize(size);
                    int width = (int) (size.x * 0.8);
                    int height = (int) (size.y * 0.8);
                    mAlertDialog.getWindow().setLayout(width, height);
                }
            });
            mAlertDialog.show();
        }
    }

    public interface ChooseDialogCallBack {
        void share(PInfo pInfo, ImageResponse imageResponse);
    }
}
